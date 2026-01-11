package com.lbgconnect.web;

import com.lbgconnect.model.ApplicationStatus;
import com.lbgconnect.model.Job;
import com.lbgconnect.model.JobApplication;
import com.lbgconnect.model.UserAccount;
import com.lbgconnect.repository.JobApplicationRepository;
import com.lbgconnect.repository.JobRepository;
import com.lbgconnect.repository.UserAccountRepository;
import com.lbgconnect.web.form.JobApplicationForm;
import com.lbgconnect.web.form.JobForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;
import java.math.BigDecimal;

@Controller
@RequiredArgsConstructor
public class JobController {

    private final JobRepository jobRepository;
    private final JobApplicationRepository applicationRepository;
    private final UserAccountRepository userRepository;

    @GetMapping("/jobs")
    public String listJobs(@RequestParam(name = "q", required = false) String q,
                           @RequestParam(name = "location", required = false) String location,
                           @RequestParam(name = "category", required = false) String category,
                           @RequestParam(name = "contractType", required = false) String contractType,
                           @RequestParam(name = "salaryMin", required = false) String salaryMin,
                           @RequestParam(name = "salaryMax", required = false) String salaryMax,
                           @RequestParam(name = "tag", required = false) String tag,
                           @RequestParam(name = "page", defaultValue = "0") int page,
                           @RequestParam(name = "size", defaultValue = "6") int size,
                           Model model) {
        Page<Job> jobsPage = jobRepository.searchJobs(
                normalize(q),
                normalize(location),
                normalize(category),
                normalize(contractType),
                parseDecimal(salaryMin),
                parseDecimal(salaryMax),
                normalize(tag),
                PageRequest.of(Math.max(page, 0), Math.max(size, 1))
        );

        model.addAttribute("jobsPage", jobsPage);
        model.addAttribute("jobs", jobsPage.getContent());
        model.addAttribute("q", q);
        model.addAttribute("location", location);
        model.addAttribute("category", category);
        model.addAttribute("contractType", contractType);
        model.addAttribute("salaryMin", salaryMin);
        model.addAttribute("salaryMax", salaryMax);
        model.addAttribute("tag", tag);
        return "jobs";
    }

    @GetMapping("/jobs/new")
    @PreAuthorize("hasRole('EMPLOYEUR')")
    public String newJob(Model model) {
        model.addAttribute("jobForm", new JobForm());
        return "job-create";
    }

    @PostMapping("/jobs")
    @PreAuthorize("hasRole('EMPLOYEUR')")
    public String createJob(@Valid @ModelAttribute("jobForm") JobForm form,
                            BindingResult bindingResult,
                            @AuthenticationPrincipal UserDetails userDetails) {
        if (bindingResult.hasErrors()) {
            return "job-create";
        }

        UserAccount poster = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        Job job = Job.builder()
                .title(form.getTitle())
                .description(form.getDescription())
                .category(form.getCategory())
                .location(form.getLocation())
                .contractType(form.getContractType())
                .salaryMin(form.getSalaryMin())
                .salaryMax(form.getSalaryMax())
                .status("Ouvert")
                .tags(parseTags(form.getTags()))
                .postedBy(poster)
                .build();

        jobRepository.save(job);
        return "redirect:/jobs?created";
    }

    @GetMapping("/jobs/{id}")
    public String jobDetail(@PathVariable("id") Long id,
                            Model model,
                            @AuthenticationPrincipal UserDetails userDetails) {
        Job job = jobRepository.findWithTagsById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        boolean alreadyApplied = false;
        if (userDetails != null) {
            UserAccount user = userRepository.findByEmail(userDetails.getUsername()).orElse(null);
            if (user != null) {
                alreadyApplied = applicationRepository
                        .findByJobIdAndApplicantId(job.getId(), user.getId())
                        .isPresent();
            }
        }

        model.addAttribute("job", job);
        model.addAttribute("applicationForm", new JobApplicationForm());
        model.addAttribute("alreadyApplied", alreadyApplied);
        return "job-detail";
    }

    @PostMapping("/jobs/{id}/apply")
    @PreAuthorize("hasAnyRole('ARTISAN','APPRENTI')")
    public String apply(@PathVariable("id") Long id,
                        @Valid @ModelAttribute("applicationForm") JobApplicationForm form,
                        BindingResult bindingResult,
                        @AuthenticationPrincipal UserDetails userDetails,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        UserAccount applicant = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        if (job.getPostedBy() != null && job.getPostedBy().getId().equals(applicant.getId())) {
            bindingResult.reject("self", "Vous ne pouvez pas postuler a votre propre offre.");
        }

        if (applicationRepository.findByJobIdAndApplicantId(job.getId(), applicant.getId()).isPresent()) {
            bindingResult.reject("alreadyApplied", "Candidature deja envoyee.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("job", job);
            model.addAttribute("alreadyApplied", false);
            return "job-detail";
        }

        applicationRepository.save(JobApplication.builder()
                .job(job)
                .applicant(applicant)
                .status(ApplicationStatus.EN_COURS)
                .coverLetter(form.getCoverLetter())
                .expectedRate(form.getExpectedRate())
                .build());

        redirectAttributes.addAttribute("applied", "true");
        return "redirect:/jobs/" + id;
    }

    private List<String> parseTags(String tags) {
        if (tags == null || tags.isBlank()) {
            return List.of();
        }
        return Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(tag -> !tag.isBlank())
                .distinct()
                .toList();
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim().toLowerCase();
    }

    private BigDecimal parseDecimal(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}

package com.lbgconnect.web;

import com.lbgconnect.model.Role;
import com.lbgconnect.model.UserAccount;
import com.lbgconnect.repository.JobRepository;
import com.lbgconnect.repository.ReviewRepository;
import com.lbgconnect.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final UserAccountRepository userRepository;
    private final JobRepository jobRepository;
    private final ReviewRepository reviewRepository;

    @GetMapping("/artisans")
    public String artisans(@RequestParam(name = "q", required = false) String q,
                           @RequestParam(name = "location", required = false) String location,
                           @RequestParam(name = "skill", required = false) String skill,
                           @RequestParam(name = "verified", required = false) Boolean verified,
                           @RequestParam(name = "ratingMin", required = false) Double ratingMin,
                           @RequestParam(name = "page", defaultValue = "0") int page,
                           @RequestParam(name = "size", defaultValue = "6") int size,
                           Model model) {
        Page<UserAccount> artisansPage = userRepository.searchByRole(
                Role.ARTISAN,
                normalize(q),
                normalize(location),
                normalize(skill),
                verified,
                ratingMin,
                PageRequest.of(Math.max(page, 0), Math.max(size, 1))
        );
        model.addAttribute("artisansPage", artisansPage);
        model.addAttribute("artisans", artisansPage.getContent());
        model.addAttribute("q", q);
        model.addAttribute("location", location);
        model.addAttribute("skill", skill);
        model.addAttribute("verified", verified);
        model.addAttribute("ratingMin", ratingMin);
        return "artisan-profile";
    }

    @GetMapping("/profile")
    public String myProfile(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        return userRepository.findByEmail(userDetails.getUsername())
                .map(user -> "redirect:/profil/" + user.getId())
                .orElse("redirect:/login");
    }

    @GetMapping("/profil/{id}")
    public String profile(@PathVariable("id") Long id, Model model) {
        UserAccount user = userRepository.findWithSkillsById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("user", user);
        model.addAttribute("reviews", reviewRepository.findByArtisanIdOrderByCreatedAtDesc(user.getId()));
        model.addAttribute("recentJobs", jobRepository.findTop3ByPostedByIdOrderByCreatedAtDesc(user.getId()));
        return "profile";
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim().toLowerCase();
    }
}

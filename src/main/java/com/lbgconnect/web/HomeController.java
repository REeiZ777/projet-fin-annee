package com.lbgconnect.web;

import com.lbgconnect.model.Role;
import com.lbgconnect.repository.JobRepository;
import com.lbgconnect.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UserAccountRepository userRepository;
    private final JobRepository jobRepository;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("artisans", userRepository.findByRole(Role.ARTISAN));
        model.addAttribute("apprentis", userRepository.findByRole(Role.APPRENTI));
        model.addAttribute("jobs", jobRepository.findAll());
        return "index";
    }

    @GetMapping("/search")
    public String search(@RequestParam(name = "q", required = false) String q,
                         @RequestParam(name = "location", required = false) String location,
                         @RequestParam(name = "type", required = false) String type) {
        String normalizedType = type == null ? "" : type.trim().toLowerCase();
        if ("artisan".equals(normalizedType) || "apprenti".equals(normalizedType)) {
            return "redirect:/artisans?q=" + safe(q) + "&location=" + safe(location);
        }
        return "redirect:/jobs?q=" + safe(q) + "&location=" + safe(location);
    }

    private String safe(String value) {
        if (value == null) {
            return "";
        }
        return URLEncoder.encode(value.trim(), StandardCharsets.UTF_8);
    }
}

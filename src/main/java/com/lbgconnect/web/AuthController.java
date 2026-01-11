package com.lbgconnect.web;

import com.lbgconnect.model.UserAccount;
import com.lbgconnect.repository.UserAccountRepository;
import com.lbgconnect.service.LocalStorageService;
import com.lbgconnect.service.StorageException;
import com.lbgconnect.web.form.RegistrationForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserAccountRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LocalStorageService storageService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        if (!model.containsAttribute("registrationForm")) {
            model.addAttribute("registrationForm", new RegistrationForm());
        }
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registrationForm") RegistrationForm form,
                           BindingResult bindingResult,
                           Model model) {
        if (userRepository.findByEmail(form.getEmail()).isPresent()) {
            bindingResult.rejectValue("email", "email.exists", "Email deja utilise.");
        }

        if (bindingResult.hasErrors()) {
            return "register";
        }

        String avatarUrl;
        try {
            avatarUrl = storageService.store(form.getAvatar());
        } catch (StorageException ex) {
            bindingResult.reject("avatar", ex.getMessage());
            return "register";
        }

        if (avatarUrl == null || avatarUrl.isBlank()) {
            avatarUrl = "/assets/images/avatar-moussa.png";
        }

        UserAccount user = UserAccount.builder()
                .fullName(form.getFullName())
                .email(form.getEmail())
                .phone(form.getPhone())
                .password(passwordEncoder.encode(form.getPassword()))
                .role(form.getRole())
                .location(form.getLocation())
                .headline(form.getHeadline())
                .bio(form.getBio())
                .avatarUrl(avatarUrl)
                .reviewsCount(0)
                .verified(false)
                .build();

        userRepository.save(user);
        return "redirect:/login?registered";
    }
}

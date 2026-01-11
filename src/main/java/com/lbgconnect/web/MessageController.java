package com.lbgconnect.web;

import com.lbgconnect.model.ConversationMessage;
import com.lbgconnect.model.UserAccount;
import com.lbgconnect.repository.ConversationMessageRepository;
import com.lbgconnect.repository.UserAccountRepository;
import com.lbgconnect.web.form.MessageForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MessageController {

    private final ConversationMessageRepository messageRepository;
    private final UserAccountRepository userRepository;

    @GetMapping("/messages")
    public String messages(@RequestParam(name = "to", required = false) String to,
                           Model model,
                           @AuthenticationPrincipal UserDetails userDetails) {
        UserAccount currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        List<ConversationMessage> messages = messageRepository
                .findBySenderIdOrRecipientIdOrderByCreatedAtDesc(currentUser.getId(), currentUser.getId());

        model.addAttribute("messages", messages);
        MessageForm form = new MessageForm();
        if (to != null && !to.isBlank()) {
            form.setRecipientEmail(to.trim());
        }
        model.addAttribute("messageForm", form);
        model.addAttribute("currentUser", currentUser);
        return "messages";
    }

    @PostMapping("/messages")
    public String sendMessage(@Valid @ModelAttribute("messageForm") MessageForm form,
                              BindingResult bindingResult,
                              Model model,
                              @AuthenticationPrincipal UserDetails userDetails) {
        UserAccount sender = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        UserAccount recipient = userRepository.findByEmail(form.getRecipientEmail())
                .orElse(null);
        if (recipient == null) {
            bindingResult.rejectValue("recipientEmail", "recipient.notFound", "Destinataire introuvable.");
        }

        if (bindingResult.hasErrors()) {
            List<ConversationMessage> messages = messageRepository
                    .findBySenderIdOrRecipientIdOrderByCreatedAtDesc(sender.getId(), sender.getId());
            model.addAttribute("messages", messages);
            model.addAttribute("currentUser", sender);
            return "messages";
        }

        messageRepository.save(ConversationMessage.builder()
                .sender(sender)
                .recipient(recipient)
                .subject(form.getSubject())
                .body(form.getBody())
                .readFlag(false)
                .build());

        return "redirect:/messages?sent";
    }
}

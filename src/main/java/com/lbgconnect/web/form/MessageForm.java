package com.lbgconnect.web.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageForm {

    @NotBlank
    @Email
    private String recipientEmail;

    @NotBlank
    @Size(max = 120)
    private String subject;

    @NotBlank
    @Size(max = 2000)
    private String body;
}

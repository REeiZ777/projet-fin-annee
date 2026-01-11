package com.lbgconnect.web.form;

import com.lbgconnect.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class RegistrationForm {

    @NotBlank
    @Size(max = 120)
    private String fullName;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, max = 64)
    private String password;

    @NotNull
    private Role role;

    @Size(max = 120)
    private String location;

    @Size(max = 40)
    private String phone;

    @Size(max = 140)
    private String headline;

    @Size(max = 1000)
    private String bio;

    private MultipartFile avatar;
}

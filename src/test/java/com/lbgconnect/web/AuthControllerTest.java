package com.lbgconnect.web;

import com.lbgconnect.model.UserAccount;
import com.lbgconnect.repository.UserAccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserAccountRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void registerCreatesUser() throws Exception {
        String email = "test.user@lbgconnect.test";

        mockMvc.perform(multipart("/register")
                        .file(new MockMultipartFile("avatar", "", "application/octet-stream", new byte[0]))
                        .param("fullName", "Test User")
                        .param("email", email)
                        .param("password", "Passw0rd!")
                        .param("role", "ARTISAN")
                        .param("location", "Abidjan")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered"));

        UserAccount user = userRepository.findByEmail(email).orElseThrow();
        assertThat(passwordEncoder.matches("Passw0rd!", user.getPassword())).isTrue();
    }

    @Test
    void registerPageLoads() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }
}

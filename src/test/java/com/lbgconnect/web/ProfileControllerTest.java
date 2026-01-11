package com.lbgconnect.web;

import com.lbgconnect.repository.UserAccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserAccountRepository userRepository;

    @Test
    @WithMockUser(username = "moussa@lbgconnect.test", roles = "ARTISAN")
    void profileRedirectsToOwnPage() throws Exception {
        Long userId = userRepository.findByEmail("moussa@lbgconnect.test").orElseThrow().getId();

        mockMvc.perform(get("/profile"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profil/" + userId));
    }
}

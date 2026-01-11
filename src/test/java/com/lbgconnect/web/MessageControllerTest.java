package com.lbgconnect.web;

import com.lbgconnect.repository.ConversationMessageRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ConversationMessageRepository messageRepository;

    @Test
    @WithMockUser(username = "adjoua@lbgconnect.test", roles = "ARTISAN")
    void sendMessagePersists() throws Exception {
        long before = messageRepository.count();

        mockMvc.perform(post("/messages")
                        .with(csrf())
                        .param("recipientEmail", "contact@lbgconnect.test")
                        .param("subject", "Hello")
                        .param("body", "Test message"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/messages?sent"));

        assertThat(messageRepository.count()).isEqualTo(before + 1);
    }
}

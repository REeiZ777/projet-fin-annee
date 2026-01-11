package com.lbgconnect.web;

import com.lbgconnect.repository.JobRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class JobControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JobRepository jobRepository;

    @Test
    @WithMockUser(username = "contact@lbgconnect.test", roles = "EMPLOYEUR")
    void createJobPersists() throws Exception {
        mockMvc.perform(post("/jobs")
                        .with(csrf())
                        .param("title", "Offre test maconnerie")
                        .param("description", "Mission simple pour un chantier de 2 semaines.")
                        .param("category", "Maconnerie")
                        .param("location", "Abidjan")
                        .param("contractType", "Projet")
                        .param("salaryMin", "200000")
                        .param("salaryMax", "300000")
                        .param("tags", "chantier, equipe"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/jobs?created"));

        assertThat(jobRepository.findByTitle("Offre test maconnerie")).isPresent();
    }

    @Test
    void listJobsRetrievesPage() throws Exception {
        mockMvc.perform(get("/jobs").param("q", "macon"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("jobsPage"));
    }
}

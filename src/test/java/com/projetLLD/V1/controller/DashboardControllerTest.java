package com.projetLLD.V1.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DashboardController.class)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "john", roles = "USER")
    void shouldReturnDashboard() throws Exception {

        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/dashboard"))
                .andExpect(model().attributeExists("username"));
    }

    @Test
    void shouldRejectUnauthenticatedUser() throws Exception {

        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isUnauthorized()); // cas le plus courant
    }
}
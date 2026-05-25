package com.projetLLD.V1.controller;

import com.projetLLD.V1.entity.User;
import com.projetLLD.V1.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    // ---------------- CREATE ----------------

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldCreateManagerAndRedirect() throws Exception {

        mockMvc.perform(post("/admin/create-manager")
                        .with(csrf())
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("email", "john@test.com")
                        .param("password", "123456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/managers"));

        verify(userService).createGestionnaire(any(User.class));
    }

    // ---------------- PAGE CREATE ----------------

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnCreateManagerPage() throws Exception {

        mockMvc.perform(get("/admin/create-manager"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/create-manager"));
    }

    // ---------------- LIST ----------------

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnManagersPage() throws Exception {

        when(userService.getAllManagers()).thenReturn(List.of());

        mockMvc.perform(get("/admin/managers"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/manager"))
                .andExpect(model().attributeExists("managers"));

        verify(userService).getAllManagers();
    }

    // ---------------- DELETE ----------------

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteManagerAndRedirect() throws Exception {

        doNothing().when(userService).deleteManager(1L);

        mockMvc.perform(post("/admin/delete-manager/1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/managers"));

        verify(userService).deleteManager(1L);
    }

    // ---------------- SECURITY ----------------

    @Test
    @WithMockUser(roles = "USER")
    void shouldForbidCreateManagerPageForNonAdmin() throws Exception {

        mockMvc.perform(get("/admin/create-manager"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldRejectCreateManagerWithoutAuth() throws Exception {

        mockMvc.perform(post("/admin/create-manager")
                        .with(csrf())
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("email", "john@test.com")
                        .param("password", "123456"))
                .andExpect(status().is3xxRedirection()); // login page
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldRejectCreateManagerWithoutCsrf() throws Exception {

        mockMvc.perform(post("/admin/create-manager")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("email", "john@test.com")
                        .param("password", "123456"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldRejectDeleteWithoutCsrf() throws Exception {

        mockMvc.perform(post("/admin/delete-manager/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldRedirectToLoginWhenNotAuthenticated() throws Exception {

        mockMvc.perform(post("/admin/create-manager")
                        .with(csrf())
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("email", "john@test.com")
                        .param("password", "123456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldForbidAdminPagesForNonAdmin() throws Exception {

        mockMvc.perform(get("/admin/managers"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/admin/create-manager"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "GESTIONNAIRE")
    void shouldForbidAdminPagesForNonAdmin2() throws Exception {

        mockMvc.perform(get("/admin/managers"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/admin/create-manager"))
                .andExpect(status().isForbidden());
    }
}
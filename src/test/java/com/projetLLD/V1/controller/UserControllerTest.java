package com.projetLLD.V1.controller;

import com.projetLLD.V1.config.SecurityConfig;
import com.projetLLD.V1.entity.User;
import com.projetLLD.V1.service.CustomUserDetailsService;
import com.projetLLD.V1.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    // =====================================================
    // REGISTER PAGE
    // =====================================================

    @Test
    void shouldReturnRegisterPage() throws Exception {

        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeExists("userRequestDTO"));
    }

    // =====================================================
    // REGISTER SUCCESS
    // =====================================================

    @Test
    void shouldRegisterUserAndRedirectToLogin() throws Exception {

        when(userService.emailExists("john@test.com"))
                .thenReturn(false);

        when(userService.register(any()))
                .thenReturn(new User());

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("email", "john@test.com")
                        .param("password", "Password123!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verify(userService).emailExists("john@test.com");
        verify(userService).register(any());
    }

    // =====================================================
    // EMAIL ALREADY EXISTS
    // =====================================================

    @Test
    void shouldReturnRegisterPageWhenEmailAlreadyExists() throws Exception {

        when(userService.emailExists("john@test.com"))
                .thenReturn(true);

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("email", "john@test.com")
                        .param("password", "Password123!"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error",
                        "Cet email est déjà utilisé"));

        verify(userService).emailExists("john@test.com");
        verify(userService, never()).register(any());
    }

    // =====================================================
    // VALIDATION ERRORS
    // =====================================================

    @Test
    void shouldReturnRegisterPageWhenValidationFails() throws Exception {

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("firstName", "")
                        .param("lastName", "")
                        .param("email", "bad-email")
                        .param("password", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeHasFieldErrors(
                        "userRequestDTO",
                        "firstName",
                        "lastName",
                        "email",
                        "password"
                ));

        verify(userService, never()).register(any());
    }

    // =====================================================
    // CSRF
    // =====================================================

    @Test
    void shouldRejectRegisterWithoutCsrf() throws Exception {

        mockMvc.perform(post("/register")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("email", "john@test.com")
                        .param("password", "Password123!"))
                .andExpect(status().isForbidden());

        verify(userService, never()).register(any());
    }

    // =====================================================
    // ANONYMOUS ACCESS
    // =====================================================

    @Test
    @WithAnonymousUser
    void shouldAllowAnonymousAccessToRegisterPage() throws Exception {

        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"));
    }
}
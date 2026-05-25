package com.projetLLD.V1.controller;

import com.projetLLD.V1.config.SecurityConfig;
import com.projetLLD.V1.dto.VehicleOptionDTO;
import com.projetLLD.V1.entity.VehicleOption;
import com.projetLLD.V1.service.CustomUserDetailsService;
import com.projetLLD.V1.service.VehicleOptionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(VehicleOptionController.class)
@Import(SecurityConfig.class)
class VehicleOptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VehicleOptionService optionService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    // =========================
    // ACCESS TESTS
    // =========================

    @Test
    void shouldRedirectWhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/vehicles/1/options"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldForbidUserAccess() throws Exception {
        mockMvc.perform(get("/vehicles/1/options"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAllowAdminAccessToList() throws Exception {
        mockMvc.perform(get("/vehicles/1/options"))
                .andExpect(status().isOk())
                .andExpect(view().name("options/list"));

        verify(optionService).getOptionsByVehicle(1L);
    }

    @Test
    @WithMockUser(roles = "GESTIONNAIRE")
    void shouldAllowManagerAccess() throws Exception {
        mockMvc.perform(get("/vehicles/1/options"))
                .andExpect(status().isOk())
                .andExpect(view().name("options/list"));
    }

    // =========================
    // CREATE FORM
    // =========================

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldShowCreateForm() throws Exception {
        mockMvc.perform(get("/vehicles/1/options/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("options/create"));
    }

    // =========================
    // CREATE
    // =========================

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateOption() throws Exception {

        mockMvc.perform(post("/vehicles/1/options")
                        .with(csrf())
                        .param("name", "GPS")
                        .param("price", "100")
                        .param("type", "CONTROLE_TECHNIQUE"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/vehicles/1/options"));

        verify(optionService, times(1))
                .createOption(eq(1L), any(VehicleOptionDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnFormWhenValidationFails() throws Exception {

        mockMvc.perform(post("/vehicles/1/options")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("options/create"));

        verify(optionService, never()).createOption(anyLong(), any());
    }

    // =========================
    // EDIT FORM
    // =========================

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldLoadEditForm() throws Exception {

        VehicleOption option = mock(VehicleOption.class);
        when(optionService.getOptionById(1L)).thenReturn(option);

        mockMvc.perform(get("/vehicles/1/options/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("options/edit"));

        verify(optionService).getOptionById(1L);
    }


    // =========================
    // DELETE
    // =========================

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteOption() throws Exception {

        mockMvc.perform(post("/vehicles/1/options/1/delete")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/vehicles/1/options"));

        verify(optionService).deleteOption(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldForbidUserDelete() throws Exception {

        mockMvc.perform(post("/vehicles/1/options/1/delete")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }
}
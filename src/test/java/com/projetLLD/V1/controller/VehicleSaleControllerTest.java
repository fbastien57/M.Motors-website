package com.projetLLD.V1.controller;

import com.projetLLD.V1.config.SecurityConfig;
import com.projetLLD.V1.dto.VehicleSaleUpdateDTO;
import com.projetLLD.V1.service.CustomUserDetailsService;
import com.projetLLD.V1.service.VehicleSaleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.FlashMap;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VehicleSaleController.class)
@Import(SecurityConfig.class)
class VehicleSaleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VehicleSaleService vehicleSaleService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    // =========================
    // ACCESS CONTROL
    // =========================

    @Test
    void shouldRedirectWhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/sales/1/edit"))
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldForbidUserRole() throws Exception {
        mockMvc.perform(get("/sales/1/edit"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAllowAdminAccess() throws Exception {

        VehicleSaleUpdateDTO dto = new VehicleSaleUpdateDTO();
        when(vehicleSaleService.getSaleForUpdate(1L)).thenReturn(dto);

        mockMvc.perform(get("/sales/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("sales/edit"));
    }

    @Test
    @WithMockUser(roles = "GESTIONNAIRE")
    void shouldAllowManagerAccess() throws Exception {
        VehicleSaleUpdateDTO dto = new VehicleSaleUpdateDTO();
        when(vehicleSaleService.getSaleForUpdate(1L)).thenReturn(dto);

        mockMvc.perform(get("/sales/1/edit"))
                .andExpect(status().isOk());
    }

    // =========================
    // EDIT FORM
    // =========================

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldLoadEditSaleForm() throws Exception {

        when(vehicleSaleService.getSaleForUpdate(1L))
                .thenReturn(new VehicleSaleUpdateDTO());

        mockMvc.perform(get("/sales/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("sales/edit"))
                .andExpect(model().attributeExists("sale"))
                .andExpect(model().attribute("saleId", 1L));

        verify(vehicleSaleService).getSaleForUpdate(1L);
    }

    // =========================
    // UPDATE
    // =========================

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateSale() throws Exception {

        mockMvc.perform(post("/sales/1/update")
                        .with(csrf())
                        .param("salePrice", "15000")
                        .param("available", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/vehicles"));

        verify(vehicleSaleService).updateSale(eq(1L), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnEditFormWhenValidationFails() throws Exception {

        mockMvc.perform(post("/sales/1/update")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("sales/edit"));

        verify(vehicleSaleService, never()).updateSale(anyLong(), any());
    }

    // =========================
    // DELETE
    // =========================

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteSaleSuccessfully() throws Exception {

        mockMvc.perform(post("/sales/1/delete")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/vehicles"))
                .andExpect(flash().attributeExists("success"));

        verify(vehicleSaleService).deleteSale(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldHandleDeleteSaleError() throws Exception {

        doThrow(new RuntimeException("Erreur suppression"))
                .when(vehicleSaleService).deleteSale(1L);

        mockMvc.perform(post("/sales/1/delete")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/vehicles"))
                .andExpect(flash().attributeExists("error"));

        verify(vehicleSaleService).deleteSale(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldForbidUserFromDeletingSale() throws Exception {

        mockMvc.perform(post("/sales/1/delete").with(csrf()))
                .andExpect(status().isForbidden());
    }
}
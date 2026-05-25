package com.projetLLD.V1.controller;

import com.projetLLD.V1.config.SecurityConfig;
import com.projetLLD.V1.entity.Vehicle;
import com.projetLLD.V1.entity.VehicleRental;
import com.projetLLD.V1.service.CustomUserDetailsService;
import com.projetLLD.V1.service.VehicleRentalService;
import com.projetLLD.V1.service.VehicleSaleService;
import com.projetLLD.V1.service.VehicleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@WebMvcTest(VehicleRentalController.class)
@Import(SecurityConfig.class)
public class VehicleRentalControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VehicleService vehicleService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private VehicleSaleService vehicleSaleService;

    @MockitoBean
    private VehicleRentalService vehicleRentalService;

    @Test
    void shouldDenyAccessWhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/vehicles/1/rentals/new"))
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldForbidAccessForUserRole() throws Exception {
        mockMvc.perform(get("/vehicles/1/rentals/new"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAllowAccessWhenAdmin() throws Exception {
        mockMvc.perform(get("/vehicles/1/rentals/new"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "GESTIONNAIRE")
    void shouldAllowAccessForManagerRole() throws Exception {
        mockMvc.perform(get("/vehicles/1/rentals/new"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateRental() throws Exception {

        mockMvc.perform(post("/vehicles/1/rentals")
                        .with(csrf())
                        .param("vehiclePrice", "1000")
                        .param("deposit", "200")
                        .param("baseMonthlyPayment", "300")
                        .param("allowedDurations", "M12", "M24")
                        .param("allowedKmPerYear", "KM10000", "KM20000")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/vehicles"));

        verify(vehicleRentalService, times(1))
                .createRental(eq(1L), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnFormWhenValidationFails() throws Exception {

        mockMvc.perform(post("/vehicles/1/rentals")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("rentals/form"));

        verify(vehicleRentalService, never()).createRental(anyLong(), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldLoadEditForm() throws Exception {

        VehicleRental rental = mock(VehicleRental.class);
        when(vehicleRentalService.getById(1L)).thenReturn(rental);

        mockMvc.perform(get("/rentals/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("rentals/edit"));

        verify(vehicleRentalService).getById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateRental() throws Exception {

        mockMvc.perform(post("/rentals/1/edit")
                        .with(csrf())
                        .param("vehiclePrice", "1200"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/vehicles"));

        verify(vehicleRentalService).updateRental(eq(1L), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteRental() throws Exception {

        mockMvc.perform(post("/rentals/1/delete")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/vehicles"));

        verify(vehicleRentalService).deleteRental(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldForbidUserFromDeleting() throws Exception {

        mockMvc.perform(post("/rentals/1/delete").with(csrf()))
                .andExpect(status().isForbidden());
    }
}

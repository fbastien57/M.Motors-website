package com.projetLLD.V1.controller;

import com.projetLLD.V1.config.SecurityConfig;
import com.projetLLD.V1.entity.Vehicle;
import com.projetLLD.V1.service.VehicleRentalService;
import com.projetLLD.V1.service.VehicleSaleService;
import com.projetLLD.V1.service.VehicleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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

@WebMvcTest(VehicleController.class)
class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VehicleService vehicleService;

    @MockitoBean
    private VehicleSaleService vehicleSaleService;

    @MockitoBean
    private VehicleRentalService vehicleRentalService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnVehicleListPage() throws Exception {

        when(vehicleService.getAllVehicles())
                .thenReturn(List.of());

        mockMvc.perform(get("/vehicles"))
                .andExpect(status().isOk())
                .andExpect(view().name("vehicle/list"))
                .andExpect(model().attributeExists("vehicles"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldShowCreateVehicleForm() throws Exception {

        mockMvc.perform(get("/vehicles/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("vehicle/newVehicle"))
                .andExpect(model().attributeExists("vehicle"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnFormWhenValidationFails() throws Exception {

        mockMvc.perform(post("/vehicles/create")
                        .with(csrf())
                        .param("brand", "") // invalid
                        .param("model", "")  // invalid
                )
                .andExpect(status().isOk())
                .andExpect(view().name("vehicle/newVehicle"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateVehicleAndRedirect() throws Exception {

        Vehicle vehicle = new Vehicle();
        vehicle.setId(1L);

        when(vehicleService.createVehicle(any())).thenReturn(vehicle);

        mockMvc.perform(post("/vehicles/create")
                        .with(csrf())
                        .param("brand", "Toyota")
                        .param("model", "Corolla")
                        .param("year", "2022")
                        .param("mileage", "10000")
                        .param("gearbox", "MANUAL")
                        .param("fuelType", "DIESEL")
                )
                .andExpect(status().is3xxRedirection());

    }

    @Test
    void shouldDenyAccessWhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/vehicles"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldShowEditVehiclePage() throws Exception {

        Vehicle vehicle = new Vehicle();
        vehicle.setId(1L);

        when(vehicleService.getVehicleById(1L)).thenReturn(vehicle);

        mockMvc.perform(get("/vehicles/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("vehicle/editVehicle"))
                .andExpect(model().attributeExists("vehicle"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateVehicleAndRedirect() throws Exception {

        mockMvc.perform(post("/vehicles/update/1")
                        .with(csrf())
                        .param("brand", "Toyota")
                        .param("model", "Yaris")
                        .param("year", "2023")
                        .param("mileage", "5000")
                        .param("gearbox", "MANUAL")
                        .param("fuelType", "DIESEL")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/vehicles/1/edit"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteVehicleAndRedirect() throws Exception {

        mockMvc.perform(post("/vehicles/1/delete")
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/vehicles"));

        verify(vehicleService).deleteVehicle(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUploadImages() throws Exception {

        MockMultipartFile file = new MockMultipartFile(
                "files",
                "test.jpg",
                "image/jpeg",
                "dummy".getBytes()
        );

        mockMvc.perform(multipart("/vehicles/1/upload-images")
                        .file(file)
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/vehicles/1/edit"));

        verify(vehicleService).addImages(eq(1L), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldShowSalesPage() throws Exception {

        when(vehicleRentalService.getRentalByVehicle(1L)).thenReturn(List.of());
        when(vehicleSaleService.getSalesByVehicle(1L)).thenReturn(List.of());

        mockMvc.perform(get("/vehicles/1/sales"))
                .andExpect(status().isOk())
                .andExpect(view().name("sales/list"))
                .andExpect(model().attributeExists("sales"))
                .andExpect(model().attributeExists("rentals"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnFormWhenSaleValidationFails() throws Exception {

        mockMvc.perform(post("/vehicles/1/sales")
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("sales/form"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnFormWhenSaleServiceThrowsException() throws Exception {

        doThrow(new RuntimeException("Error"))
                .when(vehicleSaleService)
                .createSale(anyLong(), any());

        mockMvc.perform(post("/vehicles/1/sales")
                        .with(csrf())
                        .param("price", "10000")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("sales/form"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeletePhotoAndRedirect() throws Exception {

        mockMvc.perform(post("/vehicles/1/photos/10/delete")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/vehicles/1/edit"));

        verify(vehicleService).deletePhoto(1L, 10L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldShowCreateSaleForm() throws Exception {

        mockMvc.perform(get("/vehicles/1/sales/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("sales/form"))
                .andExpect(model().attributeExists("sale"))
                .andExpect(model().attributeExists("vehicleId"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldHandleUploadImageException() throws Exception {

        MockMultipartFile file = new MockMultipartFile(
                "files",
                "test.jpg",
                "image/jpeg",
                "dummy".getBytes()
        );

        doThrow(new RuntimeException("Upload error"))
                .when(vehicleService)
                .addImages(eq(1L), any());

        mockMvc.perform(multipart("/vehicles/1/upload-images")
                        .file(file)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/vehicles/1/edit"));

        verify(vehicleService).addImages(eq(1L), any());
    }
}
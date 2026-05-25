package com.projetLLD.V1.controller;


import com.projetLLD.V1.config.UserPrincipal;
import com.projetLLD.V1.dto.CreateRequestDTO;
import com.projetLLD.V1.entity.*;
import com.projetLLD.V1.enums.RentalDuration;
import com.projetLLD.V1.enums.RequestStatus;
import com.projetLLD.V1.enums.RequestType;
import com.projetLLD.V1.enums.Role;
import com.projetLLD.V1.repository.VehicleRepository;
import com.projetLLD.V1.service.RequestService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RequestClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RequestService requestService;

    @MockitoBean
    private VehicleRepository vehicleRepository;

    // =====================================================
    // HELPER
    // =====================================================

    private UserPrincipal buildPrincipal() {

        User user = new User();

        user.setId(1L);
        user.setEmail("client@test.com");
        user.setPassword("password");
        user.setRole(Role.CLIENT);

        return new UserPrincipal(user);
    }

    // =====================================================
    // MY REQUESTS
    // =====================================================

    @Test
    void shouldReturnUserRequestsPage() throws Exception {

        UserPrincipal principal = buildPrincipal();

        Vehicle vehicle = new Vehicle();
        vehicle.setBrand("Toyota");

        Request request = new Request();
        request.setVehicle(vehicle);
        request.setType(RequestType.RENTAL);
        request.setStatus(RequestStatus.MISSING_DOCUMENT);

        when(requestService.getUserRequests(1L))
                .thenReturn(List.of(request));

        mockMvc.perform(get("/client/requests")
                        .with(user(principal)))
                .andExpect(status().isOk())
                .andExpect(view().name("requests/list"))
                .andExpect(model().attributeExists("requests"));

        verify(requestService).getUserRequests(1L);
    }


    // =====================================================
    // RENTAL REQUEST PAGE
    // =====================================================

    @Test
    void shouldReturnRentalRequestPage() throws Exception {

        UserPrincipal principal = buildPrincipal();

        VehicleRental rental = new VehicleRental();
        rental.setAllowedDurations(Set.of(RentalDuration.M24, RentalDuration.M12));

        Vehicle vehicle = new Vehicle();
        vehicle.setId(1L);
        vehicle.setRental(rental);

        when(vehicleRepository.findById(1L))
                .thenReturn(Optional.of(vehicle));

        mockMvc.perform(get("/client/requests/create/rental/1")
                        .with(user(principal)))
                .andExpect(status().isOk())
                .andExpect(view().name("requests/create-rental-request"))
                .andExpect(model().attributeExists("vehicle"));

        verify(vehicleRepository).findById(1L);
    }

    // =====================================================
    // CREATE REQUEST
    // =====================================================

    @Test
    void shouldCreateRequestAndRedirect() throws Exception {

        UserPrincipal principal = buildPrincipal();

        mockMvc.perform(post("/client/requests/create")
                        .with(user(principal))
                        .with(csrf())
                        .param("vehicleId", "1")
                        .param("message", "Je suis intéressé"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));

        verify(requestService)
                .createRequest(any(CreateRequestDTO.class), any(User.class));
    }

    // =====================================================
    // SECURITY
    // =====================================================

    @Test
    void shouldRedirectToLoginWhenAnonymous() throws Exception {

        mockMvc.perform(get("/client/requests"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void shouldRejectCreateRequestWithoutCsrf() throws Exception {

        UserPrincipal principal = buildPrincipal();

        mockMvc.perform(post("/client/requests/create")
                        .with(user(principal))
                        .param("vehicleId", "1"))
                .andExpect(status().isForbidden());
    }

    // =====================================================
    // VEHICLE NOT FOUND
    // =====================================================

    @Test
    void shouldThrowWhenVehicleNotFound() throws Exception {

        UserPrincipal principal = buildPrincipal();

        when(vehicleRepository.findById(1L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/client/requests/create/sale/1")
                        .with(user(principal)))
                .andExpect(status().is4xxClientError());

        verify(vehicleRepository).findById(1L);
    }
}
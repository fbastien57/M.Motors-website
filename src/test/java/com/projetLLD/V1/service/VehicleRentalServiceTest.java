package com.projetLLD.V1.service;

import com.projetLLD.V1.dto.VehicleRentalCreateDTO;
import com.projetLLD.V1.dto.VehicleRentalUpdateDTO;
import com.projetLLD.V1.entity.Vehicle;
import com.projetLLD.V1.entity.VehicleRental;
import com.projetLLD.V1.repository.VehicleRentalRepository;
import com.projetLLD.V1.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleRentalServiceTest {

    @Mock
    private VehicleRentalRepository vehicleRentalRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private VehicleRentalService vehicleRentalService;

    // ---------------- CREATE RENTAL ----------------

    @Test
    void shouldCreateRental() {

        Vehicle vehicle = new Vehicle();

        VehicleRentalCreateDTO dto = new VehicleRentalCreateDTO();
        dto.setVehiclePrice(3000.0);
        dto.setDeposit(500.0);
        dto.setBaseMonthlyPayment(39.9);

        when(vehicleRepository.findById(1L))
                .thenReturn(Optional.of(vehicle));

        when(vehicleRentalRepository.existsByVehicleId(1L))
                .thenReturn(false);

        vehicleRentalService.createRental(1L, dto);

        verify(vehicleRentalRepository)
                .save(any(VehicleRental.class));
    }

    @Test
    void shouldThrowWhenVehicleNotFound() {

        when(vehicleRepository.findById(1L))
                .thenReturn(Optional.empty());

        VehicleRentalCreateDTO dto =
                new VehicleRentalCreateDTO();

        assertThrows(RuntimeException.class,
                () -> vehicleRentalService.createRental(1L, dto));

        verify(vehicleRentalRepository, never())
                .save(any());
    }

    @Test
    void shouldThrowWhenRentalAlreadyExists() {

        Vehicle vehicle = new Vehicle();

        when(vehicleRepository.findById(1L))
                .thenReturn(Optional.of(vehicle));

        when(vehicleRentalRepository.existsByVehicleId(1L))
                .thenReturn(true);

        VehicleRentalCreateDTO dto =
                new VehicleRentalCreateDTO();

        assertThrows(RuntimeException.class,
                () -> vehicleRentalService.createRental(1L, dto));

        verify(vehicleRentalRepository, never())
                .save(any());
    }

    // ---------------- UPDATE RENTAL ----------------

    @Test
    void shouldUpdateRental() {

        VehicleRental rental = new VehicleRental();

        VehicleRentalUpdateDTO dto =
                new VehicleRentalUpdateDTO();

        dto.setVehiclePrice(3500.0);
        dto.setDeposit(600.0);
        dto.setBaseMonthlyPayment(49.9);
        dto.setAvailable(false);

        when(vehicleRentalRepository.findById(1L))
                .thenReturn(Optional.of(rental));

        when(vehicleRentalRepository.save(any(VehicleRental.class)))
                .thenAnswer(i -> i.getArgument(0));

        vehicleRentalService.updateRental(1L, dto);

        assertEquals(3500.0, rental.getVehiclePrice());
        assertEquals(600.0, rental.getDeposit());
        assertEquals(49.9, rental.getBaseMonthlyPayment());
        assertFalse(rental.isAvailable());

        verify(vehicleRentalRepository).save(rental);
    }

    @Test
    void shouldThrowWhenRentalNotFoundDuringUpdate() {

        when(vehicleRentalRepository.findById(1L))
                .thenReturn(Optional.empty());

        VehicleRentalUpdateDTO dto =
                new VehicleRentalUpdateDTO();

        assertThrows(RuntimeException.class,
                () -> vehicleRentalService.updateRental(1L, dto));
    }

    // ---------------- DELETE RENTAL ----------------

    @Test
    void shouldDeleteRental() {

        Vehicle vehicle = new Vehicle();

        VehicleRental rental = new VehicleRental();
        rental.setVehicle(vehicle);

        when(vehicleRentalRepository.findById(1L))
                .thenReturn(Optional.of(rental));

        vehicleRentalService.deleteRental(1L);

        assertNull(vehicle.getRental());

        verify(vehicleRentalRepository).delete(rental);
    }

    @Test
    void shouldThrowWhenRentalNotFoundDuringDelete() {

        when(vehicleRentalRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> vehicleRentalService.deleteRental(1L));

        verify(vehicleRentalRepository, never())
                .delete(any());
    }

    // ---------------- GET RENTALS BY VEHICLE ----------------

    @Test
    void shouldReturnRentalsByVehicle() {

        when(vehicleRentalRepository.findAllByVehicleId(1L))
                .thenReturn(List.of(new VehicleRental()));

        List<VehicleRental> result =
                vehicleRentalService.getRentalByVehicle(1L);

        assertEquals(1, result.size());
    }

    // ---------------- GET BY ID ----------------

    @Test
    void shouldReturnRentalById() {

        VehicleRental rental = new VehicleRental();

        when(vehicleRentalRepository.findById(1L))
                .thenReturn(Optional.of(rental));

        VehicleRental result =
                vehicleRentalService.getById(1L);

        assertNotNull(result);
    }

    @Test
    void shouldThrowWhenRentalNotFoundById() {

        when(vehicleRentalRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> vehicleRentalService.getById(1L));
    }
}
package com.projetLLD.V1.entity;

import com.projetLLD.V1.enums.FuelType;
import com.projetLLD.V1.enums.GearBox;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

class VehicleEntityTest {

    @Test
    void shouldSetAndGetFieldsCorrectly() {
        Vehicle vehicle = new Vehicle();

        vehicle.setBrand("BMW");
        vehicle.setModel("X5");
        vehicle.setYear(2020);
        vehicle.setMileage(50000);
        vehicle.setAvailable(true);

        assertEquals("BMW", vehicle.getBrand());
        assertEquals("X5", vehicle.getModel());
        assertEquals(2020, vehicle.getYear());
        assertEquals(50000, vehicle.getMileage());
        assertTrue(vehicle.isAvailable());
    }

    @Test
    void shouldSetCreatedAtOnPrePersist() {
        Vehicle vehicle = new Vehicle();

        vehicle.prePersist();

        assertNotNull(vehicle.getCreatedAt());
        assertTrue(vehicle.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }
}
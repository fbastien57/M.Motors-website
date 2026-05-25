package com.projetLLD.V1.dto;

import com.projetLLD.V1.enums.FuelType;
import com.projetLLD.V1.enums.GearBox;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class VehicleRequestDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidation_whenDtoIsValid() {
        VehicleRequestDTO dto = VehicleRequestDTO.builder()
                .brand("Toyota")
                .model("Corolla")
                .year(2022)
                .mileage(15000)
                .fuelType(FuelType.DIESEL)
                .gearbox(GearBox.AUTOMATIC)
                .description("Voiture en excellent état")
                .available(true)
                .build();

        Set<ConstraintViolation<VehicleRequestDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFail_whenBrandIsBlank() {
        VehicleRequestDTO dto = VehicleRequestDTO.builder()
                .brand("")
                .model("Corolla")
                .year(2022)
                .mileage(15000)
                .fuelType(FuelType.DIESEL)
                .gearbox(GearBox.AUTOMATIC)
                .build();

        Set<ConstraintViolation<VehicleRequestDTO>> violations = validator.validate(dto);

        assertTrue(
                violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("brand"))
        );
    }

    @Test
    void shouldFail_whenModelIsBlank() {
        VehicleRequestDTO dto = VehicleRequestDTO.builder()
                .brand("Toyota")
                .model("")
                .year(2022)
                .mileage(15000)
                .fuelType(FuelType.DIESEL)
                .gearbox(GearBox.AUTOMATIC)
                .build();

        Set<ConstraintViolation<VehicleRequestDTO>> violations = validator.validate(dto);

        assertTrue(
                violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("model"))
        );
    }

    @Test
    void shouldFail_whenFuelTypeIsNull() {
        VehicleRequestDTO dto = VehicleRequestDTO.builder()
                .brand("Toyota")
                .model("Corolla")
                .year(2022)
                .mileage(15000)
                .fuelType(null)
                .gearbox(GearBox.AUTOMATIC)
                .build();

        Set<ConstraintViolation<VehicleRequestDTO>> violations = validator.validate(dto);

        assertTrue(
                violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("fuelType"))
        );
    }

    @Test
    void shouldFail_whenGearboxIsNull() {
        VehicleRequestDTO dto = VehicleRequestDTO.builder()
                .brand("Toyota")
                .model("Corolla")
                .year(2022)
                .mileage(15000)
                .fuelType(FuelType.DIESEL)
                .gearbox(null)
                .build();

        Set<ConstraintViolation<VehicleRequestDTO>> violations = validator.validate(dto);

        assertTrue(
                violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("gearbox"))
        );
    }

    @Test
    void shouldFail_whenDescriptionTooLong() {
        String longDescription = "a".repeat(1001);

        VehicleRequestDTO dto = VehicleRequestDTO.builder()
                .brand("Toyota")
                .model("Corolla")
                .year(2022)
                .mileage(15000)
                .fuelType(FuelType.DIESEL)
                .gearbox(GearBox.AUTOMATIC)
                .description(longDescription)
                .build();

        Set<ConstraintViolation<VehicleRequestDTO>> violations = validator.validate(dto);

        assertTrue(
                violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description"))
        );
    }
}
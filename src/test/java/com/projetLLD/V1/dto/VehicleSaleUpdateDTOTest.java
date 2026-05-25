package com.projetLLD.V1.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class VehicleSaleUpdateDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidation_whenDtoIsValid() {
        VehicleSaleUpdateDTO dto = new VehicleSaleUpdateDTO();
        dto.setSalePrice(15000.0);
        dto.setAvailable(true);

        Set<ConstraintViolation<VehicleSaleUpdateDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFail_whenSalePriceIsNull() {
        VehicleSaleUpdateDTO dto = new VehicleSaleUpdateDTO();
        dto.setSalePrice(null);
        dto.setAvailable(true);

        Set<ConstraintViolation<VehicleSaleUpdateDTO>> violations = validator.validate(dto);

        assertTrue(
                violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("salePrice"))
        );
    }

    @Test
    void shouldFail_whenSalePriceIsZeroOrNegative() {
        VehicleSaleUpdateDTO dto = new VehicleSaleUpdateDTO();
        dto.setSalePrice(0.0);
        dto.setAvailable(true);

        Set<ConstraintViolation<VehicleSaleUpdateDTO>> violations = validator.validate(dto);

        assertTrue(
                violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("salePrice"))
        );
    }
}
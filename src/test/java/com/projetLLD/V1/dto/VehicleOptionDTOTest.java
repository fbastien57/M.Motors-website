package com.projetLLD.V1.dto;

import com.projetLLD.V1.enums.OptionType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class VehicleOptionDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidation_whenDtoIsValid() {
        VehicleOptionDTO dto = VehicleOptionDTO.builder()
                .name("GPS")
                .type(OptionType.ASSISTANCE_DEPANNAGE)
                .price(199.99)
                .description("GPS intégré avec écran tactile")
                .build();

        Set<ConstraintViolation<VehicleOptionDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFail_whenNameIsBlank() {
        VehicleOptionDTO dto = VehicleOptionDTO.builder()
                .name("")
                .type(OptionType.ENTRETIEN_ET_SAV)
                .price(100.0)
                .build();

        Set<ConstraintViolation<VehicleOptionDTO>> violations = validator.validate(dto);

        assertTrue(
                violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name"))
        );
    }

    @Test
    void shouldFail_whenTypeIsNull() {
        VehicleOptionDTO dto = VehicleOptionDTO.builder()
                .name("GPS")
                .type(null)
                .price(100.0)
                .build();

        Set<ConstraintViolation<VehicleOptionDTO>> violations = validator.validate(dto);

        assertTrue(
                violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("type"))
        );
    }

    @Test
    void shouldFail_whenPriceIsNegative() {
        VehicleOptionDTO dto = VehicleOptionDTO.builder()
                .name("GPS")
                .type(OptionType.ASSISTANCE_DEPANNAGE)
                .price(-10.0)
                .build();

        Set<ConstraintViolation<VehicleOptionDTO>> violations = validator.validate(dto);

        assertTrue(
                violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("price"))
        );
    }

    @Test
    void shouldFail_whenDescriptionTooLong() {
        String longDescription = "a".repeat(1001);

        VehicleOptionDTO dto = VehicleOptionDTO.builder()
                .name("GPS")
                .type(OptionType.ENTRETIEN_ET_SAV)
                .price(100.0)
                .description(longDescription)
                .build();

        Set<ConstraintViolation<VehicleOptionDTO>> violations = validator.validate(dto);

        assertTrue(
                violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description"))
        );
    }
}
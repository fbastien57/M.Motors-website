package com.projetLLD.V1.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserRequestDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidation_whenDtoIsValid() {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setEmail("test@test.com");
        dto.setPassword("Password@1234"); // valide (majuscule + spécial + >=12)
        dto.setFirstName("John");
        dto.setLastName("Doe");

        Set<ConstraintViolation<UserRequestDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFail_whenEmailIsInvalid() {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setEmail("invalid-email");
        dto.setPassword("Password@1234");
        dto.setFirstName("John");
        dto.setLastName("Doe");

        Set<ConstraintViolation<UserRequestDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(
                violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email"))
        );
    }

    @Test
    void shouldFail_whenPasswordTooShort() {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setEmail("test@test.com");
        dto.setPassword("Ab@12"); // trop court
        dto.setFirstName("John");
        dto.setLastName("Doe");

        Set<ConstraintViolation<UserRequestDTO>> violations = validator.validate(dto);

        assertTrue(
                violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password"))
        );
    }

    @Test
    void shouldFail_whenFirstNameBlank() {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setEmail("test@test.com");
        dto.setPassword("Password@1234");
        dto.setFirstName("");
        dto.setLastName("Doe");

        Set<ConstraintViolation<UserRequestDTO>> violations = validator.validate(dto);

        assertTrue(
                violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("firstName"))
        );
    }
}
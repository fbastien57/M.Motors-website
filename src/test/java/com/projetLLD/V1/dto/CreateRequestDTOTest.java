package com.projetLLD.V1.dto;

import com.projetLLD.V1.enums.RequestType;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CreateRequestDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldFailWhenVehicleIdIsNull() {
        CreateRequestDTO dto = new CreateRequestDTO();
        dto.setType(RequestType.RENTAL);

        var violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void shouldPassWhenValid() {
        CreateRequestDTO dto = new CreateRequestDTO();
        dto.setVehicleId(1L);
        dto.setType(RequestType.RENTAL);
        dto.setOptionIds(List.of(1L, 2L));

        var violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }
}
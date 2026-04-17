package com.projetLLD.V1.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehicleSaleUpdateDTO {

    @NotNull(message = "Le prix est obligatoire")
    @Positive(message = "Le prix doit être supérieur à 0")
    private Double salePrice;

    private boolean available;
}

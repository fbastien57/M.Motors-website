package com.projetLLD.V1.dto;

import com.projetLLD.V1.enums.OptionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleOptionDTO {

    @NotBlank
    private String name;

    @NotNull
    private OptionType type;

    @NotNull
    @PositiveOrZero
    private Double price;

    @Size(max = 1000)
    private String description;

}

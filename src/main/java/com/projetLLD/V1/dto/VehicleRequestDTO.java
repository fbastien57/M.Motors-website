package com.projetLLD.V1.dto;

import com.projetLLD.V1.enums.FuelType;
import com.projetLLD.V1.enums.GearBox;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleRequestDTO {

    @NotBlank
    private String brand;

    @NotBlank
    private String model;

    @NotNull
    private int year;

    @NotNull
    private int mileage;

    @NotNull
    private FuelType fuelType;

    @NotNull
    private GearBox gearbox;

    @Size(max = 1000)
    private String description;

    private boolean available;

}

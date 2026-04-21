package com.projetLLD.V1.dto;

import com.projetLLD.V1.enums.AnnualMileage;
import com.projetLLD.V1.enums.RentalDuration;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class VehicleRentalCreateDTO {

    @NotNull
    @Positive
    private Double vehiclePrice;

    private Double deposit;

    @Positive
    private Double baseMonthlyPayment;

    @NotNull
    private Set<RentalDuration> allowedDurations;

    @NotNull
    private Set<AnnualMileage> allowedKmPerYear;
}

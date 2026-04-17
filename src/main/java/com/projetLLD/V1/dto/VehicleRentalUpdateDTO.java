package com.projetLLD.V1.dto;

import com.projetLLD.V1.enums.AnnualMileage;
import com.projetLLD.V1.enums.RentalDuration;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class VehicleRentalUpdateDTO {

    private Long id;

    @Positive
    private Double vehiclePrice;

    private Double deposit;

    @Positive
    private Double baseMonthlyPayment;

    private boolean available;

    private Set<RentalDuration> allowedDurations;

    private Set<AnnualMileage> allowedKmPerYear;
}

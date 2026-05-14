package com.projetLLD.V1.dto;

import com.projetLLD.V1.enums.AnnualMileage;
import com.projetLLD.V1.enums.RentalDuration;
import com.projetLLD.V1.enums.RequestType;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@RequiredArgsConstructor
public class CreateRequestDTO {

    private Long vehicleId;

    private RequestType type;
    private List<Long> optionIds = new ArrayList<>();

    // RENTAL
    private RentalDuration duration;
    private AnnualMileage kmPerYear;
    private Double deposit;

    // SALE
    private Boolean financing;
}

package com.projetLLD.V1.entity;

import com.projetLLD.V1.enums.AnnualMileage;
import com.projetLLD.V1.enums.RentalDuration;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "request_rental_details")
public class RequestRentalDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "request_id", nullable = false, unique = true)
    private Request request;

    @Enumerated(EnumType.STRING)
    private RentalDuration duration;

    @Enumerated(EnumType.STRING)
    private AnnualMileage kmPerYear;

    private Double deposit;

    private Double monthlyPayment;

    private Double residualPrice;
}

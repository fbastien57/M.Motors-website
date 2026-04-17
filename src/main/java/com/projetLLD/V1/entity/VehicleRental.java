package com.projetLLD.V1.entity;

import com.projetLLD.V1.enums.AnnualMileage;
import com.projetLLD.V1.enums.RentalDuration;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "vehicle_rentals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleRental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //véhicule lié
    @OneToOne
    @JoinColumn(name = "vehicle_id", nullable = false, unique = true)
    private Vehicle vehicle;

    //prix du véhicule (base LOA)
    @Column(nullable = false)
    private Double vehiclePrice;

    //apport initial (optionnel)
    private Double deposit;

    //durées possibles (12, 24, 36, 48 mois)
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "vehicle_rental_durations",
            joinColumns = @JoinColumn(name = "rental_id")
    )
    @Column(name = "duration")
    private Set<RentalDuration> allowedDurations;

    //kilométrage annuel (10k, 15k, 20k)
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "vehicle_rental_mileage",
            joinColumns = @JoinColumn(name = "rental_id")
    )
    @Column(name = "mileage")
    private Set<AnnualMileage> allowedKmPerYear;

    private Double baseMonthlyPayment;

    private boolean available = true;
}

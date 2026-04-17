package com.projetLLD.V1.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vehicle_sales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleSale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double salePrice;

    @Column(name = "is_available", nullable = false)
    private boolean available = true;

    @OneToOne
    @JoinColumn(name = "vehicle_id", nullable = false, unique = true)
    private Vehicle vehicle;
}

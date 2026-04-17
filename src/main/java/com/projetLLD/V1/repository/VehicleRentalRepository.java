package com.projetLLD.V1.repository;

import com.projetLLD.V1.entity.VehicleRental;
import com.projetLLD.V1.entity.VehicleSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRentalRepository extends JpaRepository<VehicleRental, Long> {

    Optional<VehicleRental> findByVehicleId(Long vehicleId);
    List<VehicleRental> findAllByVehicleId(Long vehicleId);
    boolean existsByVehicleId(Long vehicleId);
}

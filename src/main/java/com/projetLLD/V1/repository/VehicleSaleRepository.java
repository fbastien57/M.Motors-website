package com.projetLLD.V1.repository;

import com.projetLLD.V1.entity.VehicleSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleSaleRepository extends JpaRepository<VehicleSale, Long> {

    Optional<VehicleSale> findByVehicleId(Long vehicleId);
    List<VehicleSale> findAllByVehicleId(Long vehicleId);
}

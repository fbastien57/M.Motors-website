package com.projetLLD.V1.repository;

import com.projetLLD.V1.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

@Repository
public interface VehicleRepository  extends JpaRepository<Vehicle, Long> {

    @Query("""
    SELECT v FROM Vehicle v
    JOIN FETCH v.sale s
    WHERE s.available = true AND v.available = true
    """)
    List<Vehicle> findVehiclesForSale();

    @Query("""
    SELECT v FROM Vehicle v
    JOIN v.rental r
    WHERE r.available = true AND v.available = true
    """)
    List<Vehicle> findVehiclesForRental();

}

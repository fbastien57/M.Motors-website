package com.projetLLD.V1.service;

import com.projetLLD.V1.dto.VehicleRentalCreateDTO;
import com.projetLLD.V1.dto.VehicleRentalUpdateDTO;
import com.projetLLD.V1.entity.Vehicle;
import com.projetLLD.V1.entity.VehicleRental;
import com.projetLLD.V1.entity.VehicleSale;
import com.projetLLD.V1.repository.VehicleRentalRepository;
import com.projetLLD.V1.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleRentalService {

    private final VehicleRentalRepository vehicleRentalRepository;
    private final VehicleRepository vehicleRepository;

    public void createRental(Long vehicleId, VehicleRentalCreateDTO dto) {

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Véhicule introuvable"));

        if (vehicleRentalRepository.existsByVehicleId(vehicleId)) {
            throw new RuntimeException("Déjà une offre de location");
        }

        VehicleRental rental = new VehicleRental();

        rental.setVehicle(vehicle);
        rental.setVehiclePrice(dto.getVehiclePrice());
        rental.setDeposit(dto.getDeposit());

        rental.setBaseMonthlyPayment(dto.getBaseMonthlyPayment());

        rental.setAllowedDurations(dto.getAllowedDurations());
        rental.setAllowedKmPerYear(dto.getAllowedKmPerYear());

        rental.setAvailable(true);

        vehicleRentalRepository.save(rental);
    }

    public void updateRental(Long id, VehicleRentalUpdateDTO dto) {

        VehicleRental rental = vehicleRentalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rental introuvable"));

        rental.setVehiclePrice(dto.getVehiclePrice());
        rental.setDeposit(dto.getDeposit());

        // 💰 IMPORTANT
        rental.setBaseMonthlyPayment(dto.getBaseMonthlyPayment());

        rental.setAvailable(dto.isAvailable());
        rental.setAllowedDurations(dto.getAllowedDurations());
        rental.setAllowedKmPerYear(dto.getAllowedKmPerYear());

        vehicleRentalRepository.save(rental);
    }

    // DELETE
    public void deleteRental(Long id) {

        VehicleRental rental = vehicleRentalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rental introuvable"));

        Vehicle vehicle = rental.getVehicle();
        vehicle.setRental(null);

        vehicleRentalRepository.delete(rental);
    }

    public List<VehicleRental> getRentalByVehicle(Long vehicleId) {
        return vehicleRentalRepository.findAllByVehicleId(vehicleId);
    }

    public VehicleRental getById(Long id) {
        return vehicleRentalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offre de location introuvable"));
    }
}

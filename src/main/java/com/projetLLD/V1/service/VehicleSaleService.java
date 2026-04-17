package com.projetLLD.V1.service;

import com.projetLLD.V1.dto.VehicleSaleCreateDTO;
import com.projetLLD.V1.dto.VehicleSaleUpdateDTO;
import com.projetLLD.V1.entity.Vehicle;
import com.projetLLD.V1.entity.VehicleSale;
import com.projetLLD.V1.repository.VehicleRepository;
import com.projetLLD.V1.repository.VehicleSaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleSaleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleSaleRepository vehicleSaleRepository;

    public void createSale(Long vehicleId, VehicleSaleCreateDTO dto) {

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Véhicule introuvable"));

        if (vehicleSaleRepository.findByVehicleId(vehicleId).isPresent()) {
            throw new RuntimeException("Déjà en vente");
        }

        VehicleSale sale = new VehicleSale();
        sale.setSalePrice(dto.getSalePrice());
        sale.setAvailable(true);
        sale.setVehicle(vehicle);

        vehicleSaleRepository.save(sale);
    }

    public void updateSale(Long saleId, VehicleSaleUpdateDTO dto) {

        VehicleSale sale = vehicleSaleRepository.findById(saleId)
                .orElseThrow(() -> new RuntimeException("Sale not found"));

        sale.setSalePrice(dto.getSalePrice());
        sale.setAvailable(dto.isAvailable());

        vehicleSaleRepository.save(sale);
    }

    public VehicleSaleUpdateDTO getSaleForUpdate(Long id) {
        VehicleSale sale = vehicleSaleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found"));

        VehicleSaleUpdateDTO dto = new VehicleSaleUpdateDTO();
        dto.setSalePrice(sale.getSalePrice());
        dto.setAvailable(sale.isAvailable());

        return dto;
    }

    @Transactional
    public void deleteSale(Long id) {

        VehicleSale sale = vehicleSaleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found"));

        Vehicle vehicle = sale.getVehicle();
        vehicle.setSale(null);

        vehicleSaleRepository.delete(sale);
    }

    public List<VehicleSale> getSalesByVehicle(Long vehicleId) {
        return vehicleSaleRepository.findAllByVehicleId(vehicleId);
    }
}

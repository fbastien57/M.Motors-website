package com.projetLLD.V1.service;

import com.projetLLD.V1.dto.VehicleOptionDTO;
import com.projetLLD.V1.entity.Vehicle;
import com.projetLLD.V1.entity.VehicleOption;
import com.projetLLD.V1.repository.VehicleOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleOptionService {

    private final VehicleOptionRepository optionRepository;
    private final VehicleService vehicleService;

    // ✅ CREATE
    public VehicleOption createOption(Long vehicleId, VehicleOptionDTO dto) {

        Vehicle vehicle = vehicleService.getVehicleById(vehicleId);

        VehicleOption option = VehicleOption.builder()
                .name(dto.getName())
                .type(dto.getType())
                .price(dto.getPrice())
                .description(dto.getDescription())
                .vehicle(vehicle)
                .build();

        return optionRepository.save(option);
    }

    // ✅ GET ALL BY VEHICLE
    public List<VehicleOption> getOptionsByVehicle(Long vehicleId) {
        return optionRepository.findByVehicleId(vehicleId);
    }

    // ✅ GET BY ID
    public VehicleOption getOptionById(Long id) {
        return optionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Option not found"));
    }

    // ✅ UPDATE
    public VehicleOption updateOption(Long id, VehicleOptionDTO dto) {

        VehicleOption option = getOptionById(id);

        option.setName(dto.getName());
        option.setType(dto.getType());
        option.setPrice(dto.getPrice());
        option.setDescription(dto.getDescription());

        return optionRepository.save(option);
    }

    // ✅ DELETE
    public void deleteOption(Long id) {
        VehicleOption option = getOptionById(id);
        optionRepository.delete(option);
    }
}

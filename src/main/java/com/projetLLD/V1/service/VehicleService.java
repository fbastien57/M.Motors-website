package com.projetLLD.V1.service;

import com.projetLLD.V1.dto.VehiclePhotoDTO;
import com.projetLLD.V1.dto.VehicleRequestDTO;
import com.projetLLD.V1.entity.Vehicle;
import com.projetLLD.V1.entity.VehiclePhoto;
import com.projetLLD.V1.mapper.VehicleMapper;
import com.projetLLD.V1.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;

    private final String uploadDir = "uploads/vehicles/";

    public Vehicle createVehicle(VehicleRequestDTO dto) throws Exception {
        Vehicle vehicle = vehicleMapper.toEntity(dto);

        if (vehicle.getPhotos() == null) {
            vehicle.setPhotos(new ArrayList<>());
        }

        // Gérer les photos
        if (dto.getPhotos() != null && !dto.getPhotos().isEmpty()) {
            for (VehiclePhotoDTO photoDTO : dto.getPhotos()) {
                MultipartFile file = photoDTO.getFile();
                if (file != null && !file.isEmpty()) {
                    String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
                    Path path = Paths.get(uploadDir + filename);
                    Files.createDirectories(path.getParent());
                    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                    VehiclePhoto photo = VehiclePhoto.builder()
                            .fileName(filename)
                            .filePath(path.toString())
                            .isMain(photoDTO.getIsMain())
                            .orderIndex(photoDTO.getOrderIndex())
                            .vehicle(vehicle)
                            .build();

                    vehicle.getPhotos().add(photo);
                }
            }
        }

        return vehicleRepository.save(vehicle);
    }
}

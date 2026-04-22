package com.projetLLD.V1.service;

import com.projetLLD.V1.dto.VehicleRequestDTO;
import com.projetLLD.V1.entity.Vehicle;
import com.projetLLD.V1.entity.VehiclePhoto;
import com.projetLLD.V1.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    private final String uploadDir = "uploads/vehicles/";

    // ✅ CREATE VEHICLE (SANS IMAGES)
    public Vehicle createVehicle(VehicleRequestDTO dto) {
        Vehicle vehicle = new Vehicle();

        vehicle.setBrand(dto.getBrand());
        vehicle.setModel(dto.getModel());
        vehicle.setYear(dto.getYear());
        vehicle.setMileage(dto.getMileage());
        vehicle.setFuelType(dto.getFuelType());
        vehicle.setGearbox(dto.getGearbox());
        vehicle.setDescription(dto.getDescription());
        vehicle.setAvailable(dto.isAvailable());

        return vehicleRepository.save(vehicle);
    }

    // ✅ AJOUTER DES IMAGES À UN VÉHICULE
    public void addImages(Long vehicleId, MultipartFile[] files) throws IOException {

        Vehicle vehicle = getVehicleById(vehicleId);

        if (files == null || files.length == 0) return;

        int startIndex = vehicle.getPhotos().size();

        for (int i = 0; i < files.length; i++) {

            MultipartFile file = files[i];

            if (file.isEmpty()) continue;

            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = Paths.get(uploadDir, filename);

            // 🔐 crée le dossier si inexistant
            Files.createDirectories(path.getParent());

            // 💾 sauvegarde fichier
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            // 🧱 création entité photo
            VehiclePhoto photo = VehiclePhoto.builder()
                    .fileName(filename)
                    .filePath(path.toString())
                    .isMain(startIndex + i == 0) // première image = principale
                    .orderIndex(startIndex + i)
                    .vehicle(vehicle)
                    .build();

            vehicle.getPhotos().add(photo);
        }

        vehicleRepository.save(vehicle);
    }

    public void deletePhoto(Long vehicleId, Long photoId) {

        Vehicle vehicle = getVehicleById(vehicleId);

        VehiclePhoto photoToDelete = vehicle.getPhotos()
                .stream()
                .filter(p -> p.getId().equals(photoId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Photo not found"));

        // 1. supprimer fichier physique
        try {
            Path path = Paths.get(photoToDelete.getFilePath());
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException("Erreur suppression fichier", e);
        }

        // 2. supprimer en base
        vehicle.getPhotos().remove(photoToDelete);

        vehicleRepository.save(vehicle);
    }

    // ✅ GET ALL
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    // ✅ GET BY ID
    public Vehicle getVehicleById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
    }

    // ✅ UPDATE
    public Vehicle updateVehicle(Long id, VehicleRequestDTO dto) {

        Vehicle existing = getVehicleById(id);

        existing.setBrand(dto.getBrand());
        existing.setModel(dto.getModel());
        existing.setYear(dto.getYear());
        existing.setMileage(dto.getMileage());
        existing.setFuelType(dto.getFuelType());
        existing.setGearbox(dto.getGearbox());
        existing.setDescription(dto.getDescription());
        existing.setAvailable(dto.isAvailable());

        return vehicleRepository.save(existing);
    }

    // ✅ DELETE VEHICLE
    public void deleteVehicle(Long id) {

        Vehicle vehicle = getVehicleById(id);

        // 1. supprimer les fichiers physiques
        for (VehiclePhoto photo : vehicle.getPhotos()) {
            try {
                Path path = Paths.get(photo.getFilePath());
                Files.deleteIfExists(path);
            } catch (IOException e) {
                System.out.println("Erreur suppression fichier: " + photo.getFileName());
            }
        }

        vehicleRepository.delete(vehicle);
    }

    public List<Vehicle> getVehiclesForSale() {
        return vehicleRepository.findVehiclesForSale();
    }

    public List<Vehicle> getVehiclesForRental() {
        return vehicleRepository.findVehiclesForRental();
    }
}
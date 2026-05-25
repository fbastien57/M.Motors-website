package com.projetLLD.V1.service;

import com.projetLLD.V1.dto.VehicleRequestDTO;
import com.projetLLD.V1.entity.Vehicle;
import com.projetLLD.V1.entity.VehiclePhoto;
import com.projetLLD.V1.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private VehicleService vehicleService;

    // ---------------- CREATE ----------------

    @Test
    void shouldCreateVehicle() {
        VehicleRequestDTO dto = new VehicleRequestDTO();
        dto.setBrand("BMW");
        dto.setModel("X5");
        dto.setYear(2020);
        dto.setMileage(10000);
        dto.setAvailable(true);

        when(vehicleRepository.save(any(Vehicle.class)))
                .thenAnswer(i -> i.getArgument(0));

        Vehicle result = vehicleService.createVehicle(dto);

        assertEquals("BMW", result.getBrand());
        assertEquals("X5", result.getModel());
        assertTrue(result.isAvailable());
    }

    // ---------------- GET BY ID ----------------

    @Test
    void shouldReturnVehicleById() {
        Vehicle vehicle = new Vehicle();

        when(vehicleRepository.findById(1L))
                .thenReturn(Optional.of(vehicle));

        Vehicle result = vehicleService.getVehicleById(1L);

        assertNotNull(result);
    }

    @Test
    void shouldThrowWhenVehicleNotFound() {
        when(vehicleRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> vehicleService.getVehicleById(1L));
    }

    // ---------------- UPDATE ----------------

    @Test
    void shouldUpdateVehicle() {
        Vehicle existing = new Vehicle();

        VehicleRequestDTO dto = new VehicleRequestDTO();
        dto.setBrand("Audi");
        dto.setModel("A4");

        when(vehicleRepository.findById(1L))
                .thenReturn(Optional.of(existing));

        when(vehicleRepository.save(any(Vehicle.class)))
                .thenAnswer(i -> i.getArgument(0));

        Vehicle result = vehicleService.updateVehicle(1L, dto);

        assertEquals("Audi", result.getBrand());
        assertEquals("A4", result.getModel());
    }

    // ---------------- DELETE ----------------

    @Test
    void shouldDeleteVehicle() {
        Vehicle vehicle = new Vehicle();

        when(vehicleRepository.findById(1L))
                .thenReturn(Optional.of(vehicle));

        vehicle.setPhotos(new ArrayList<>());

        vehicleService.deleteVehicle(1L);

        verify(vehicleRepository).delete(vehicle);
    }

    // ---------------- LIST ----------------

    @Test
    void shouldGetAllVehicles() {
        when(vehicleRepository.findAll())
                .thenReturn(List.of(new Vehicle()));

        List<Vehicle> result = vehicleService.getAllVehicles();

        assertEquals(1, result.size());
    }

    // ---------------- FILTER ----------------

    @Test
    void shouldGetVehiclesForSale() {
        when(vehicleRepository.findVehiclesForSale())
                .thenReturn(List.of(new Vehicle()));

        assertEquals(1, vehicleService.getVehiclesForSale().size());
    }

    @Test
    void shouldGetVehiclesForRental() {
        when(vehicleRepository.findVehiclesForRental())
                .thenReturn(List.of(new Vehicle()));

        assertEquals(1, vehicleService.getVehiclesForRental().size());
    }


    @Test
    void shouldSkipAddImagesWhenNull() throws IOException {
        Vehicle vehicle = new Vehicle();
        vehicle.setPhotos(new ArrayList<>());

        when(vehicleRepository.findById(1L))
                .thenReturn(Optional.of(vehicle));

        vehicleService.addImages(1L, null);

        verify(vehicleRepository, times(0)).save(any());
    }

    // ---------------- DELETE PHOTO ----------------

    @Test
    void shouldThrowWhenPhotoNotFound() {
        Vehicle vehicle = new Vehicle();
        vehicle.setPhotos(new ArrayList<>());

        when(vehicleRepository.findById(1L))
                .thenReturn(Optional.of(vehicle));

        assertThrows(RuntimeException.class,
                () -> vehicleService.deletePhoto(1L, 99L));
    }

    @Test
    void shouldAddImagesToVehicle() throws IOException {

        Vehicle vehicle = new Vehicle();
        vehicle.setPhotos(new ArrayList<>());

        MultipartFile file = mock(MultipartFile.class);

        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("test.jpg");
        when(file.getInputStream())
                .thenReturn(new ByteArrayInputStream("data".getBytes()));

        when(vehicleRepository.findById(1L))
                .thenReturn(Optional.of(vehicle));

        when(vehicleRepository.save(any(Vehicle.class)))
                .thenAnswer(i -> i.getArgument(0));

        vehicleService.addImages(1L, new MultipartFile[]{file});

        assertEquals(1, vehicle.getPhotos().size());

        verify(vehicleRepository).save(vehicle);
    }

    @Test
    void shouldSkipEmptyFiles() throws IOException {

        Vehicle vehicle = new Vehicle();
        vehicle.setPhotos(new ArrayList<>());

        MultipartFile emptyFile = mock(MultipartFile.class);

        when(emptyFile.isEmpty()).thenReturn(true);

        when(vehicleRepository.findById(1L))
                .thenReturn(Optional.of(vehicle));

        vehicleService.addImages(1L,
                new MultipartFile[]{emptyFile});

        assertEquals(0, vehicle.getPhotos().size());

        verify(vehicleRepository).save(vehicle);
    }

    @Test
    void shouldDeletePhoto() {

        VehiclePhoto photo = VehiclePhoto.builder()
                .id(1L)
                .filePath("fake/path.jpg")
                .build();

        Vehicle vehicle = new Vehicle();
        vehicle.setPhotos(new ArrayList<>(List.of(photo)));

        when(vehicleRepository.findById(1L))
                .thenReturn(Optional.of(vehicle));

        vehicleService.deletePhoto(1L, 1L);

        assertEquals(0, vehicle.getPhotos().size());

        verify(vehicleRepository).save(vehicle);
    }
}
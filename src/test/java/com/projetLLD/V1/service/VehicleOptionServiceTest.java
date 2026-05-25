package com.projetLLD.V1.service;

import com.projetLLD.V1.dto.VehicleOptionDTO;
import com.projetLLD.V1.entity.Vehicle;
import com.projetLLD.V1.entity.VehicleOption;
import com.projetLLD.V1.enums.OptionType;
import com.projetLLD.V1.repository.VehicleOptionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleOptionServiceTest {

    @Mock
    private VehicleOptionRepository optionRepository;

    @Mock
    private VehicleService vehicleService;

    @InjectMocks
    private VehicleOptionService vehicleOptionService;

    // ---------------- CREATE OPTION ----------------

    @Test
    void shouldCreateOption() {

        Vehicle vehicle = new Vehicle();

        VehicleOptionDTO dto = new VehicleOptionDTO();
        dto.setName("GPS");
        dto.setType(OptionType.CONTROLE_TECHNIQUE);
        dto.setPrice(50.0);
        dto.setDescription("GPS intégré");

        when(vehicleService.getVehicleById(1L))
                .thenReturn(vehicle);

        when(optionRepository.save(any(VehicleOption.class)))
                .thenAnswer(i -> i.getArgument(0));

        VehicleOption result =
                vehicleOptionService.createOption(1L, dto);

        assertEquals("GPS", result.getName());
        assertEquals(OptionType.CONTROLE_TECHNIQUE, result.getType());
        assertEquals(50.0, result.getPrice());
        assertEquals("GPS intégré", result.getDescription());
        assertEquals(vehicle, result.getVehicle());

        verify(optionRepository).save(any(VehicleOption.class));
    }

    // ---------------- GET OPTIONS BY VEHICLE ----------------

    @Test
    void shouldReturnOptionsByVehicle() {

        when(optionRepository.findByVehicleId(1L))
                .thenReturn(List.of(new VehicleOption()));

        List<VehicleOption> result =
                vehicleOptionService.getOptionsByVehicle(1L);

        assertEquals(1, result.size());
    }

    // ---------------- GET OPTION BY ID ----------------

    @Test
    void shouldReturnOptionById() {

        VehicleOption option = new VehicleOption();

        when(optionRepository.findById(1L))
                .thenReturn(Optional.of(option));

        VehicleOption result =
                vehicleOptionService.getOptionById(1L);

        assertNotNull(result);
    }

    @Test
    void shouldThrowWhenOptionNotFound() {

        when(optionRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> vehicleOptionService.getOptionById(1L));
    }

    // ---------------- UPDATE OPTION ----------------

    @Test
    void shouldUpdateOption() {

        VehicleOption option = new VehicleOption();

        VehicleOptionDTO dto = new VehicleOptionDTO();
        dto.setName("Toit ouvrant");
        dto.setType(OptionType.ASSISTANCE_DEPANNAGE);
        dto.setPrice(120.0);
        dto.setDescription("Toit panoramique");

        when(optionRepository.findById(1L))
                .thenReturn(Optional.of(option));

        when(optionRepository.save(any(VehicleOption.class)))
                .thenAnswer(i -> i.getArgument(0));

        VehicleOption result =
                vehicleOptionService.updateOption(1L, dto);

        assertEquals("Toit ouvrant", result.getName());
        assertEquals(OptionType.ASSISTANCE_DEPANNAGE, result.getType());
        assertEquals(120.0, result.getPrice());
        assertEquals("Toit panoramique", result.getDescription());

        verify(optionRepository).save(option);
    }

    @Test
    void shouldThrowWhenUpdatingUnknownOption() {

        when(optionRepository.findById(1L))
                .thenReturn(Optional.empty());

        VehicleOptionDTO dto = new VehicleOptionDTO();

        assertThrows(RuntimeException.class,
                () -> vehicleOptionService.updateOption(1L, dto));

        verify(optionRepository, never()).save(any());
    }

    // ---------------- DELETE OPTION ----------------

    @Test
    void shouldDeleteOption() {

        VehicleOption option = new VehicleOption();

        when(optionRepository.findById(1L))
                .thenReturn(Optional.of(option));

        vehicleOptionService.deleteOption(1L);

        verify(optionRepository).delete(option);
    }

    @Test
    void shouldThrowWhenDeletingUnknownOption() {

        when(optionRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> vehicleOptionService.deleteOption(1L));

        verify(optionRepository, never()).delete(any());
    }
}
package com.projetLLD.V1.service;

import com.projetLLD.V1.dto.VehicleSaleCreateDTO;
import com.projetLLD.V1.dto.VehicleSaleUpdateDTO;
import com.projetLLD.V1.entity.Vehicle;
import com.projetLLD.V1.entity.VehicleSale;
import com.projetLLD.V1.repository.VehicleRepository;
import com.projetLLD.V1.repository.VehicleSaleRepository;
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
class VehicleSaleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private VehicleSaleRepository vehicleSaleRepository;

    @InjectMocks
    private VehicleSaleService vehicleSaleService;

    // ---------------- CREATE SALE ----------------

    @Test
    void shouldCreateSale() {

        Vehicle vehicle = new Vehicle();

        VehicleSaleCreateDTO dto = new VehicleSaleCreateDTO();
        dto.setSalePrice(2500.0);

        when(vehicleRepository.findById(1L))
                .thenReturn(Optional.of(vehicle));

        when(vehicleSaleRepository.findByVehicleId(1L))
                .thenReturn(Optional.empty());

        vehicleSaleService.createSale(1L, dto);

        verify(vehicleSaleRepository).save(any(VehicleSale.class));
    }

    @Test
    void shouldThrowWhenVehicleNotFound() {

        VehicleSaleCreateDTO dto = new VehicleSaleCreateDTO();

        when(vehicleRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> vehicleSaleService.createSale(1L, dto));

        verify(vehicleSaleRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenVehicleAlreadyOnSale() {

        Vehicle vehicle = new Vehicle();

        when(vehicleRepository.findById(1L))
                .thenReturn(Optional.of(vehicle));

        when(vehicleSaleRepository.findByVehicleId(1L))
                .thenReturn(Optional.of(new VehicleSale()));

        VehicleSaleCreateDTO dto = new VehicleSaleCreateDTO();

        assertThrows(RuntimeException.class,
                () -> vehicleSaleService.createSale(1L, dto));

        verify(vehicleSaleRepository, never()).save(any());
    }

    // ---------------- UPDATE SALE ----------------

    @Test
    void shouldUpdateSale() {

        VehicleSale sale = new VehicleSale();

        VehicleSaleUpdateDTO dto = new VehicleSaleUpdateDTO();
        dto.setSalePrice(3000.0);
        dto.setAvailable(false);

        when(vehicleSaleRepository.findById(1L))
                .thenReturn(Optional.of(sale));

        when(vehicleSaleRepository.save(any(VehicleSale.class)))
                .thenAnswer(i -> i.getArgument(0));

        vehicleSaleService.updateSale(1L, dto);

        assertEquals(3000.0, sale.getSalePrice());
        assertFalse(sale.isAvailable());

        verify(vehicleSaleRepository).save(sale);
    }

    @Test
    void shouldThrowWhenSaleNotFoundDuringUpdate() {

        when(vehicleSaleRepository.findById(1L))
                .thenReturn(Optional.empty());

        VehicleSaleUpdateDTO dto = new VehicleSaleUpdateDTO();

        assertThrows(RuntimeException.class,
                () -> vehicleSaleService.updateSale(1L, dto));
    }

    // ---------------- GET SALE FOR UPDATE ----------------

    @Test
    void shouldReturnSaleUpdateDTO() {

        VehicleSale sale = new VehicleSale();
        sale.setSalePrice(2000.0);
        sale.setAvailable(true);

        when(vehicleSaleRepository.findById(1L))
                .thenReturn(Optional.of(sale));

        VehicleSaleUpdateDTO result =
                vehicleSaleService.getSaleForUpdate(1L);

        assertEquals(2000.0, result.getSalePrice());
        assertTrue(result.isAvailable());
    }

    @Test
    void shouldThrowWhenSaleNotFoundForUpdateDTO() {

        when(vehicleSaleRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> vehicleSaleService.getSaleForUpdate(1L));
    }

    // ---------------- DELETE SALE ----------------

    @Test
    void shouldDeleteSale() {

        Vehicle vehicle = new Vehicle();

        VehicleSale sale = new VehicleSale();
        sale.setVehicle(vehicle);

        when(vehicleSaleRepository.findById(1L))
                .thenReturn(Optional.of(sale));

        vehicleSaleService.deleteSale(1L);

        assertNull(vehicle.getSale());

        verify(vehicleSaleRepository).delete(sale);
    }

    @Test
    void shouldThrowWhenSaleNotFoundDuringDelete() {

        when(vehicleSaleRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> vehicleSaleService.deleteSale(1L));

        verify(vehicleSaleRepository, never()).delete(any());
    }

    // ---------------- GET SALES BY VEHICLE ----------------

    @Test
    void shouldReturnSalesByVehicle() {

        when(vehicleSaleRepository.findAllByVehicleId(1L))
                .thenReturn(List.of(new VehicleSale()));

        List<VehicleSale> result =
                vehicleSaleService.getSalesByVehicle(1L);

        assertEquals(1, result.size());
    }
}
package com.projetLLD.V1.service;

import com.projetLLD.V1.dto.CreateRequestDTO;
import com.projetLLD.V1.entity.*;
import com.projetLLD.V1.enums.*;
import com.projetLLD.V1.repository.RequestRepository;
import com.projetLLD.V1.repository.VehicleOptionRepository;
import com.projetLLD.V1.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.within;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceTest {

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private VehicleOptionRepository vehicleOptionRepository;

    @InjectMocks
    private RequestService requestService;

    // ---------------- CREATE RENTAL REQUEST ----------------

    @Test
    void shouldCreateRentalRequest() {

        User user = new User();

        VehicleRental rental = new VehicleRental();
        rental.setDeposit(5000.0);
        rental.setBaseMonthlyPayment(399.0);

        Vehicle vehicle = new Vehicle();
        vehicle.setRental(rental);

        VehicleOption option = VehicleOption.builder()
                .name("GPS")
                .price(500)
                .type(OptionType.ENTRETIEN_ET_SAV)
                .build();

        CreateRequestDTO dto = new CreateRequestDTO();
        dto.setVehicleId(1L);
        dto.setType(RequestType.RENTAL);
        dto.setDuration(RentalDuration.M12);
        dto.setKmPerYear(AnnualMileage.KM10000);
        dto.setOptionIds(List.of(10L));

        when(vehicleRepository.findById(1L))
                .thenReturn(Optional.of(vehicle));

        when(vehicleOptionRepository.findById(10L))
                .thenReturn(Optional.of(option));

        when(requestRepository.save(any(Request.class)))
                .thenAnswer(i -> i.getArgument(0));

        Request result = requestService.createRequest(dto, user);

        assertEquals(RequestType.RENTAL, result.getType());

        assertEquals(RequestStatus.MISSING_DOCUMENT,
                result.getStatus());

        assertNotNull(result.getRentalDetails());

        assertEquals(RentalDuration.M12,
                result.getRentalDetails().getDuration());

        assertEquals(AnnualMileage.KM10000,
                result.getRentalDetails().getKmPerYear());

        assertEquals(5000.0,
                result.getRentalDetails().getDeposit());

        assertEquals(399,
                result.getRentalDetails().getMonthlyPayment());

        assertEquals(1, result.getOptions().size());

        assertEquals("GPS",
                result.getOptions().get(0).getName());

        assertEquals(5, result.getDocuments().size());

        assertTrue(result.getDocuments()
                .stream()
                .anyMatch(doc -> doc.getType() == DocumentType.LOA_FORM));

        verify(requestRepository).save(any(Request.class));
    }

    // ---------------- CREATE SALE REQUEST ----------------

    @Test
    void shouldCreateSaleRequest() {

        User user = new User();

        VehicleSale sale = new VehicleSale();
        sale.setSalePrice(2500.0);

        Vehicle vehicle = new Vehicle();
        vehicle.setSale(sale);

        CreateRequestDTO dto = new CreateRequestDTO();
        dto.setVehicleId(1L);
        dto.setType(RequestType.SALE);
        dto.setFinancing(true);

        when(vehicleRepository.findById(1L))
                .thenReturn(Optional.of(vehicle));

        when(requestRepository.save(any(Request.class)))
                .thenAnswer(i -> i.getArgument(0));

        Request result = requestService.createRequest(dto, user);

        assertEquals(RequestType.SALE, result.getType());

        assertEquals(RequestStatus.MISSING_DOCUMENT,
                result.getStatus());

        assertNotNull(result.getSaleDetails());

        assertEquals(2500.0,
                result.getSaleDetails().getSalePrice());

        assertTrue(result.getSaleDetails().getFinancing());

        assertEquals(5, result.getDocuments().size());

        assertTrue(result.getDocuments()
                .stream()
                .anyMatch(doc -> doc.getType() == DocumentType.SALE_FORM));

        verify(requestRepository).save(any(Request.class));
    }

    // ---------------- CREATE REQUEST ERRORS ----------------

    @Test
    void shouldThrowWhenVehicleNotFound() {

        CreateRequestDTO dto = new CreateRequestDTO();
        dto.setVehicleId(1L);
        dto.setType(RequestType.SALE);

        when(vehicleRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> requestService.createRequest(dto, new User()));

        verify(requestRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenTypeIsNull() {

        Vehicle vehicle = new Vehicle();

        CreateRequestDTO dto = new CreateRequestDTO();
        dto.setVehicleId(1L);

        when(vehicleRepository.findById(1L))
                .thenReturn(Optional.of(vehicle));

        assertThrows(IllegalArgumentException.class,
                () -> requestService.createRequest(dto, new User()));

        verify(requestRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenVehicleNotAvailableForRental() {

        Vehicle vehicle = new Vehicle();

        CreateRequestDTO dto = new CreateRequestDTO();
        dto.setVehicleId(1L);
        dto.setType(RequestType.RENTAL);

        when(vehicleRepository.findById(1L))
                .thenReturn(Optional.of(vehicle));

        assertThrows(IllegalStateException.class,
                () -> requestService.createRequest(dto, new User()));

        verify(requestRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenVehicleNotAvailableForSale() {

        Vehicle vehicle = new Vehicle();

        CreateRequestDTO dto = new CreateRequestDTO();
        dto.setVehicleId(1L);
        dto.setType(RequestType.SALE);

        when(vehicleRepository.findById(1L))
                .thenReturn(Optional.of(vehicle));

        assertThrows(IllegalStateException.class,
                () -> requestService.createRequest(dto, new User()));

        verify(requestRepository, never()).save(any());
    }

    // ---------------- GET USER REQUESTS ----------------

    @Test
    void shouldReturnUserRequests() {

        when(requestRepository.findByUserId(1L))
                .thenReturn(List.of(new Request()));

        List<Request> result =
                requestService.getUserRequests(1L);

        assertEquals(1, result.size());
    }

    // ---------------- GET REQUEST FOR USER ----------------

    @Test
    void shouldReturnRequestForCorrectUser() {

        User user = new User();
        user.setId(1L);

        Request request = Request.builder()
                .user(user)
                .build();

        when(requestRepository.findById(1L))
                .thenReturn(Optional.of(request));

        Request result =
                requestService.getRequestForUser(1L, 1L);

        assertEquals(request, result);
    }

    @Test
    void shouldThrowWhenUserUnauthorized() {

        User user = new User();
        user.setId(1L);

        Request request = Request.builder()
                .user(user)
                .build();

        when(requestRepository.findById(1L))
                .thenReturn(Optional.of(request));

        assertThrows(RuntimeException.class,
                () -> requestService.getRequestForUser(1L, 2L));
    }

    // ---------------- APPROVE REQUEST ----------------

    @Test
    void shouldApproveRequest() {

        RequestDocument doc = RequestDocument.builder()
                .status(DocumentStatus.VALIDATED)
                .build();

        Request request = Request.builder()
                .documents(List.of(doc))
                .build();

        when(requestRepository.findById(1L))
                .thenReturn(Optional.of(request));

        requestService.approveRequest(1L);

        assertEquals(RequestStatus.APPROVED,
                request.getStatus());
    }

    @Test
    void shouldThrowWhenDocumentsNotValidated() {

        RequestDocument doc = RequestDocument.builder()
                .status(DocumentStatus.MISSING)
                .build();

        Request request = Request.builder()
                .documents(List.of(doc))
                .build();

        when(requestRepository.findById(1L))
                .thenReturn(Optional.of(request));

        assertThrows(IllegalStateException.class,
                () -> requestService.approveRequest(1L));
    }

    // ---------------- REJECT REQUEST ----------------

    @Test
    void shouldRejectRequest() {

        Request request = Request.builder().build();

        when(requestRepository.findById(1L))
                .thenReturn(Optional.of(request));

        requestService.rejectRequest(1L);

        assertEquals(RequestStatus.REJECTED,
                request.getStatus());
    }

    // ---------------- DELETE REQUEST ----------------

    @Test
    void shouldDeleteRequestAndFiles() {

        RequestDocument doc = RequestDocument.builder()
                .filePath("test.pdf")
                .build();

        Request request = Request.builder()
                .documents(List.of(doc))
                .build();

        when(requestRepository.findById(1L))
                .thenReturn(Optional.of(request));

        requestService.deleteRequest(1L);

        verify(fileStorageService).delete("test.pdf");

        verify(requestRepository).delete(request);
    }

    @Test
    void shouldDeleteRequestWithoutFiles() {

        RequestDocument doc = RequestDocument.builder()
                .filePath(null)
                .build();

        Request request = Request.builder()
                .documents(List.of(doc))
                .build();

        when(requestRepository.findById(1L))
                .thenReturn(Optional.of(request));

        requestService.deleteRequest(1L);

        verify(fileStorageService, never()).delete(any());

        verify(requestRepository).delete(request);
    }

    @Test
    void shouldThrowWhenDeletingUnknownRequest() {

        when(requestRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> requestService.deleteRequest(1L));

        verify(requestRepository, never()).delete(any());
    }

    @Test
    void shouldBuildRequestWithAllFields() {

        User user = new User();
        Vehicle vehicle = new Vehicle();

        RequestRentalDetails rentalDetails = new RequestRentalDetails();
        RequestSaleDetails saleDetails = new RequestSaleDetails();

        List<RequestOption> options = List.of();
        List<RequestDocument> documents = List.of();

        LocalDateTime now = LocalDateTime.now();

        Request request = Request.builder()
                .id(1L)
                .user(user)
                .vehicle(vehicle)
                .type(RequestType.RENTAL)
                .status(RequestStatus.PENDING)
                .options(options)
                .documents(documents)
                .totalPrice(15000.0)
                .comment("Test comment")
                .createdAt(LocalDateTime.now())
                .updatedAt(now)
                .rentalDetails(rentalDetails)
                .saleDetails(saleDetails)
                .build();

        assertThat(request.getId()).isEqualTo(1L);
        assertThat(request.getUser()).isEqualTo(user);
        assertThat(request.getVehicle()).isEqualTo(vehicle);
        assertThat(request.getType()).isEqualTo(RequestType.RENTAL);
        assertThat(request.getStatus()).isEqualTo(RequestStatus.PENDING);
        assertThat(request.getOptions()).isEqualTo(options);
        assertThat(request.getDocuments()).isEqualTo(documents);
        assertThat(request.getTotalPrice()).isEqualTo(15000.0);
        assertThat(request.getComment()).isEqualTo("Test comment");
        assertThat(request.getCreatedAt()).isNotNull();
        assertThat(request.getUpdatedAt()).isNotNull();
        assertThat(request.getRentalDetails()).isEqualTo(rentalDetails);
        assertThat(request.getSaleDetails()).isEqualTo(saleDetails);
    }
}
package com.projetLLD.V1.service;

import com.projetLLD.V1.dto.CreateRequestDTO;
import com.projetLLD.V1.entity.*;
import com.projetLLD.V1.enums.DocumentType;
import com.projetLLD.V1.enums.DocumentStatus;
import com.projetLLD.V1.enums.RequestStatus;
import com.projetLLD.V1.enums.RequestType;
import com.projetLLD.V1.repository.RequestOptionRepository;
import com.projetLLD.V1.repository.RequestRepository;
import com.projetLLD.V1.repository.VehicleOptionRepository;
import com.projetLLD.V1.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;
    private final VehicleRepository vehicleRepository;
    private final VehicleOptionRepository vehicleOptionRepository;

    @Transactional
    public Request createRequest(CreateRequestDTO dto, User user) {

        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow();

        RequestType type = dto.getType();

        if (type == null) {
            throw new IllegalArgumentException("Request type is required");
        }

        // sécurité cohérence véhicule
        if (type == RequestType.RENTAL && vehicle.getRental() == null) {
            throw new IllegalStateException("Vehicle not available for rental");
        }

        if (type == RequestType.SALE && vehicle.getSale() == null) {
            throw new IllegalStateException("Vehicle not available for sale");
        }

        Request request = Request.builder()
                .user(user)
                .vehicle(vehicle)
                .type(type)
                .status(RequestStatus.MISSING_DOCUMENT)
                .build();

        if (type == RequestType.RENTAL) {

            List<RequestOption> options = Optional.ofNullable(dto.getOptionIds())
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(id -> vehicleOptionRepository.findById(id)
                            .orElseThrow())
                    .map(opt -> RequestOption.builder()
                            .name(opt.getName())
                            .price(opt.getPrice())
                            .type(opt.getType())
                            .request(request)
                            .build())
                    .toList();

            request.setOptions(options);

            RequestRentalDetails rental = RequestRentalDetails.builder()
                    .request(request)
                    .duration(dto.getDuration())
                    .kmPerYear(dto.getKmPerYear())
                    .deposit(vehicle.getRental().getDeposit())
                    .monthlyPayment(vehicle.getRental().getBaseMonthlyPayment())
                    .build();

            request.setRentalDetails(rental);
        }

        // SALE
        if (type == RequestType.SALE) {

            Boolean financing = dto.getFinancing() != null && dto.getFinancing();

            RequestSaleDetails sale = RequestSaleDetails.builder()
                    .request(request)
                    .salePrice(vehicle.getSale().getSalePrice())
                    .financing(financing)
                    .build();

            request.setSaleDetails(sale);
        }

        request.setDocuments(buildRequiredDocuments(request));

        return requestRepository.save(request);
    }

    public List<Request> getUserRequests(Long userId) {
        return requestRepository.findByUserId(userId);
    }

    public Request getRequestForUser(Long requestId, Long userId) {

        Request request = requestRepository.findById(requestId)
                .orElseThrow();

        if (!request.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        return request;
    }

    private List<RequestDocument> buildRequiredDocuments(Request request) {

        List<RequestDocument> docs = new ArrayList<>();

        // DOCUMENTS COMMUNS
        docs.add(createDocument(request, DocumentType.ID_CARD));

        docs.add(createDocument(request, DocumentType.DRIVER_LICENSE));

        docs.add(createDocument(request, DocumentType.PROOF_OF_INCOME));

        docs.add(createDocument(request, DocumentType.PROOF_OF_ADDRESS));

        // LOCATION
        if (request.getType() == RequestType.RENTAL) {
            docs.add(createDocument(request, DocumentType.LOA_FORM));
        }

        // ACHAT
        if (request.getType() == RequestType.SALE) {
            docs.add(createDocument(request, DocumentType.SALE_FORM));
        }

        return docs;
    }

    private RequestDocument createDocument(Request request,
                                           DocumentType type) {

        return RequestDocument.builder()
                .request(request)
                .type(type)
                .status(DocumentStatus.MISSING)
                .build();
    }

    @Transactional
    public void approveRequest(Long requestId) {

        Request request = requestRepository.findById(requestId)
                .orElseThrow();

        boolean allValidated = request.getDocuments().stream()
                .allMatch(doc -> doc.getStatus() == DocumentStatus.VALIDATED);

        if (!allValidated) {
            throw new IllegalStateException(
                    "Tous les documents doivent être validés"
            );
        }

        request.setStatus(RequestStatus.APPROVED);
    }

    @Transactional
    public void rejectRequest(Long requestId) {

        Request request = requestRepository.findById(requestId)
                .orElseThrow();

        request.setStatus(RequestStatus.REJECTED);
    }
}

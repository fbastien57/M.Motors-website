package com.projetLLD.V1.dto;

import com.projetLLD.V1.enums.RequestStatus;
import com.projetLLD.V1.enums.RequestType;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class RequestResponseDTO {

    private Long id;
    private RequestType type;
    private RequestStatus status;
    private Double totalPrice;
}

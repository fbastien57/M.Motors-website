package com.projetLLD.V1.dto;

import com.projetLLD.V1.enums.DocumentStatus;
import com.projetLLD.V1.enums.DocumentType;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor
public class RequestDocumentDTO {

    private Long id;
    private DocumentType type;
    private DocumentStatus status;
    private String fileName;
    private String rejectionReason;
}

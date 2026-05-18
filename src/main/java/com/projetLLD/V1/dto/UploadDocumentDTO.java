package com.projetLLD.V1.dto;

import com.projetLLD.V1.enums.DocumentType;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@RequiredArgsConstructor
public class UploadDocumentDTO {

    private DocumentType type;

    private MultipartFile file;
}

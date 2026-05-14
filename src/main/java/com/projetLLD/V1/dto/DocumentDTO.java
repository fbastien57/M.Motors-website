package com.projetLLD.V1.dto;

import com.projetLLD.V1.enums.DocumentCategory;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class DocumentDTO {

    private Long id;

    private String name;

    private String description;

    private DocumentCategory category;

    private MultipartFile file;
}

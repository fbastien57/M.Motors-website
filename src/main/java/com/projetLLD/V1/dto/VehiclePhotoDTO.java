package com.projetLLD.V1.dto;


import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VehiclePhotoDTO {

    private MultipartFile file;
    private Boolean isMain;
    private Integer orderIndex;
}

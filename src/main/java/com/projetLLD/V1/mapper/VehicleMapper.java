package com.projetLLD.V1.mapper;

import com.projetLLD.V1.dto.VehicleRequestDTO;
import com.projetLLD.V1.entity.Vehicle;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VehicleMapper {

    Vehicle toEntity(VehicleRequestDTO dto);

    VehicleRequestDTO toDTO(Vehicle entity);

}

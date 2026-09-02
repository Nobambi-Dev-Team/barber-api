package com.nobambidevteam.barberApi.modules.branch.mapper;

import com.nobambidevteam.barberApi.modules.branch.dto.BranchCreateDto;
import com.nobambidevteam.barberApi.modules.branch.dto.BranchDto;
import com.nobambidevteam.barberApi.modules.branch.entity.BranchEntity;


public class BranchMapper {

    private BranchMapper() {
        throw new IllegalStateException("Clase de utilidad");
    }

    // DTO to Entity
    public static BranchEntity toEntity(BranchCreateDto dto) {
        if (dto == null) return null;

        return BranchEntity.builder()
                .name(dto.name())
                .address(dto.address())
                .phone(dto.phone())
                .timezone(dto.timezone() != null && !dto.timezone().isBlank() ? dto.timezone() : "America/Argentina/Buenos_Aires")
                .mapIframeUrl(dto.mapIframeUrl())
                .googleMapsUrl(dto.googleMapsUrl())
                .build();

    }

    // Entity to DTO
    public static BranchDto toDto(BranchEntity entity) {
        if (entity == null) return null;

        return new BranchDto(
                entity.getId(),
                entity.getName(),
                entity.getAddress(),
                entity.getPhone(),
                entity.getTimezone(),
                entity.getMapIframeUrl(),
                entity.getGoogleMapsUrl(),
                entity.isActive()
        );

    }
}

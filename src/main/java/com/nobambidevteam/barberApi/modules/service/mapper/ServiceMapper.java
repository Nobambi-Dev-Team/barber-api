package com.nobambidevteam.barberApi.modules.service.mapper;

import com.nobambidevteam.barberApi.modules.service.dto.ServiceCreateDto;
import com.nobambidevteam.barberApi.modules.service.dto.ServiceDto;
import com.nobambidevteam.barberApi.modules.service.entity.ServiceEntity;

import java.math.BigDecimal;

public class ServiceMapper {

    private ServiceMapper() {
        throw new IllegalStateException("Clase de utilidad");
    }

    // DTO to Entity
    public static ServiceEntity toEntity(ServiceCreateDto dto) {
        if (dto == null) return null;

        return ServiceEntity.builder()
                .name(dto.name())
                .description(dto.description())
                .price(dto.price() != null ? dto.price() : BigDecimal.ZERO)
                .category(dto.category() != null && !dto.category().isBlank() ? dto.category() : "Cortes")
                .durationMinutes(dto.durationMinutes() != null ? dto.durationMinutes() : 0)
                .bufferMinutes(dto.bufferMinutes() != null ? dto.bufferMinutes() : 0)
                .isRecommended(dto.isRecommended() != null ? dto.isRecommended() : false)
                .isActive(true)
                .build();
    }

    // Entity to DTO
    public static ServiceDto toDto(ServiceEntity entity) {
        if (entity == null) return null;

        return new ServiceDto(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getCategory(),
                entity.getDurationMinutes(),
                entity.getBufferMinutes(),
                entity.isRecommended(),
                entity.isActive()
        );
    }
}

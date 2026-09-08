package com.nobambidevteam.barberApi.modules.service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ServiceUpdateDto(

        @Size(min = 1, max = 255, message = "El nombre del servicio no debe superar los 255 caracteres")
        String name,

        @Size(max = 1000, message = "La descripción no debe superar los 1000 caracteres")
        String description,

        @DecimalMin(value = "0.00", message = "El precio no puede ser negativo")
        BigDecimal price,

        @Size(min = 1, max = 100, message = "La categoría no debe superar los 100 caracteres")
        String category,

        @Min(value = 1, message = "La duración debe ser de al menos 1 minuto")
        Integer durationMinutes,

        @Min(value = 0, message = "El tiempo de descanso no puede ser negativo")
        Integer bufferMinutes,

        Boolean isRecommended,

        Boolean isActive
) {}

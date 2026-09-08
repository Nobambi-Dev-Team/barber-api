package com.nobambidevteam.barberApi.modules.service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ServiceCreateDto(

        @NotBlank(message = "El nombre del servicio es obligatorio")
        @Size(max = 255, message = "El nombre del servicio no debe superar los 255 caracteres")
        String name,

        @Size(max = 1000, message = "La descripción del servicio no debe superar los 1000 caracteres")
        String description,

        @NotNull(message = "El precio del servicio es obligatorio")
        @DecimalMin(value = "0.00", message = "El precio del servicio debe ser un valor positivo")
        BigDecimal price,

        @NotBlank(message = "La categoría del servicio es obligatoria")
        @Size(max = 100, message = "La categoría del servicio no debe superar los 100 caracteres")
        String category,

        @NotNull(message = "La duración del servicio es obligatoria")
        @Min(value = 1, message = "La duración del servicio debe ser al menos 1 minuto")
        @Max(value = 1440, message = "La duración del servicio no debe superar los 1440 minutos (24 horas)")
        Integer durationMinutes,

        @Min(value = 0, message = "El tiempo de limpieza/descanso post-turno no puede ser negativo")
        @Max(value = 1440, message = "El tiempo de limpieza/descanso post-turno no debe superar los 1440 minutos (24 horas)")
        Integer bufferMinutes,

        Boolean isRecommended
) {
    public ServiceCreateDto {
        if (bufferMinutes == null) {
            bufferMinutes = 0;
        }
        if (isRecommended == null) {
            isRecommended = false;
        }
    }
}
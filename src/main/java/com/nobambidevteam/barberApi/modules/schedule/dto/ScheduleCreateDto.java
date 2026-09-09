package com.nobambidevteam.barberApi.modules.schedule.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;
import java.util.UUID;

public record ScheduleCreateDto (
        @NotNull(message = "El ID del barbero es obligatorio")
        UUID staffId,

        @NotNull(message = "El ID de la sucursal es obligatorio")
        UUID branchId,

        @Min(value = 0, message = "El día de la semana no puede ser menor a 0 (Domingo)")
        @Max(value = 6, message = "El día de la semana no puede ser mayor a 6 (Sábado)")
        int dayOfWeek,

        @NotNull(message = "La hora de inicio es obligatoria")
        LocalTime startTime,

        @NotNull(message = "La hora de finalización es obligatoria")
        LocalTime endTime
){}

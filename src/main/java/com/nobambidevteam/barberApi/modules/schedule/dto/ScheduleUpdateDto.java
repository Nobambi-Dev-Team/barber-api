package com.nobambidevteam.barberApi.modules.schedule.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalTime;
import java.util.UUID;

public record ScheduleUpdateDto (

        UUID staffId,
        UUID branchId,

        @Min(value = 0, message = "El día de la semana no puede ser menor a 0 (Domingo)")
        @Max(value = 6, message = "El día de la semana no puede ser mayor a 6 (Sábado)")
        Integer dayOfWeek,

        LocalTime startTime,
        LocalTime endTime,

        Boolean isActive
){
}

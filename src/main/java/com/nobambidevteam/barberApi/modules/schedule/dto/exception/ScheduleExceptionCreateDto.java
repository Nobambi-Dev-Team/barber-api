package com.nobambidevteam.barberApi.modules.schedule.dto.exception;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalTime;
import java.util.UUID;

public record ScheduleExceptionCreateDto(
        @NotNull(message = "El ID del staff es obligatorio")
        UUID staffId,

        @NotNull(message = "El ID de la sucursal es obligatorio")
        UUID branchId,

        @NotNull(message = "La fecha y hora de inicio son obligatorias")
        Instant startDateTime,

        @NotNull(message = "La fecha y hora de fin son obligatorias")
        Instant endDateTime,

        @NotNull(message = "Debe indicar si es una sobreescritura de horario laboral (isWorkingOverride)")
        Boolean isWorkingOverride,

        LocalTime overrideStartTime,
        LocalTime overrideEndTime,

        String reason
) {}
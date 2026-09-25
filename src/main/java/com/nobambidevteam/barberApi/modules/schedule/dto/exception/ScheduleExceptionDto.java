package com.nobambidevteam.barberApi.modules.schedule.dto.exception;

import java.time.Instant;
import java.time.LocalTime;
import java.util.UUID;

public record ScheduleExceptionDto(
        UUID id,
        UUID staffId,
        UUID branchId,
        Instant startDateTime,
        Instant endDateTime,
        boolean isWorkingOverride,
        LocalTime overrideStartTime,
        LocalTime overrideEndTime,
        String reason
) {}
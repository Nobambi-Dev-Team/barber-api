package com.nobambidevteam.barberApi.modules.schedule.dto.exception;

import java.time.Instant;
import java.time.LocalTime;

public record ScheduleExceptionUpdateDto(
        Instant startDateTime,
        Instant endDateTime,
        Boolean isWorkingOverride,
        LocalTime overrideStartTime,
        LocalTime overrideEndTime,
        String reason
) {}
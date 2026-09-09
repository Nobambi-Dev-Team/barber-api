package com.nobambidevteam.barberApi.modules.schedule.dto;

import java.time.LocalTime;
import java.util.UUID;

public record ScheduleDto(
        UUID id,

        UUID staffId,
        UUID branchId,

        int dayOfWeek,
        LocalTime startTime,
        LocalTime endTime,

        boolean isActive
) {
}

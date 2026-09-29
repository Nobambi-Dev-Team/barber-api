package com.nobambidevteam.barberApi.modules.appointment.dto;

import com.nobambidevteam.barberApi.modules.appointment.enums.AppointmentStatus;

import java.time.Instant;
import java.util.UUID;

public record AppointmentDto(
        UUID id,
        UUID branchId,
        UUID staffId,
        UUID serviceId,
        UUID customerId,
        Instant startAt,
        Instant endAt,
        AppointmentStatus status,
        String notes,
        String cancelReason
) {}

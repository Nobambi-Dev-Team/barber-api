package com.nobambidevteam.barberApi.modules.appointment.dto;

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
        String status,
        String notes,
        String cancelReason
) {}

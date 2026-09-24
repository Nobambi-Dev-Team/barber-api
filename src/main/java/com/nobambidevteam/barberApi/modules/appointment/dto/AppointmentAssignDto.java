package com.nobambidevteam.barberApi.modules.appointment.dto;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record AppointmentAssignDto(
        @NotNull UUID branchId,
        @NotNull UUID staffId,
        @NotNull UUID serviceId,
        @NotNull UUID customerId,
        @NotNull Instant startAt,
        String notes
) {}

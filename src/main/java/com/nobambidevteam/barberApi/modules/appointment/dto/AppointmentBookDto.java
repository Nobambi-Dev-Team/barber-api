package com.nobambidevteam.barberApi.modules.appointment.dto;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record AppointmentBookDto(
        @NotNull UUID branchId,
        @NotNull UUID staffId,
        @NotNull UUID serviceId,
        String customerFirstName,
        String customerLastName,
        String customerPhone,
        String customerEmail,
        @NotNull Instant startAt,
        String notes
){}

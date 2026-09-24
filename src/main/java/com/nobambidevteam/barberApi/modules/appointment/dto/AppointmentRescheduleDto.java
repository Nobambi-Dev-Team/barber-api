package com.nobambidevteam.barberApi.modules.appointment.dto;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record AppointmentRescheduleDto(
        @NotNull(message = "La nueva fecha de inicio es obligatoria")
        Instant startAt
) {}

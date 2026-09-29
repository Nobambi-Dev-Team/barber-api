package com.nobambidevteam.barberApi.modules.appointment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record OtpVerifyRequestDto(
        @NotNull UUID appointmentId,
        @NotBlank String code
) {}

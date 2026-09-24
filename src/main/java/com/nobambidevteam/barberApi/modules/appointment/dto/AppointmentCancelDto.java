package com.nobambidevteam.barberApi.modules.appointment.dto;

import jakarta.validation.constraints.NotBlank;

public record AppointmentCancelDto(
        @NotBlank(message = "Debe proporcionar un motivo de cancelación")
        String reason
) {}

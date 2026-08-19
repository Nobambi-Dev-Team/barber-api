package com.nobambidevteam.barberApi.modules.security.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"username, message, jwt, status"})
public record AuthLoginResponseDto(

        String username,
        String message,
        String jwt,
        boolean status
) {
}

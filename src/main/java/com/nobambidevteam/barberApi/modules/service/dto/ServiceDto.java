package com.nobambidevteam.barberApi.modules.service.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ServiceDto(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        String category,
        int durationMinutes,
        int bufferMinutes,
        boolean isRecommended,
        boolean isActive
) {}

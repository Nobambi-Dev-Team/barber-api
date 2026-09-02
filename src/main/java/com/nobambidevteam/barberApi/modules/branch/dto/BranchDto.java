package com.nobambidevteam.barberApi.modules.branch.dto;

import java.util.UUID;

public record BranchDto(
        UUID id,

        String name,
        String address,
        String phone,

        String timezone,
        String mapIframeUrl,
        String googleMapsUrl,

        boolean isActive
)
{}

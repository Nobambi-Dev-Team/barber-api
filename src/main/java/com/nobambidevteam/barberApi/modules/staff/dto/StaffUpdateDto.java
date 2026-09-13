package com.nobambidevteam.barberApi.modules.staff.dto;

import java.util.Set;
import java.util.UUID;

public record StaffUpdateDto(
        String firstName,
        String lastName,
        String phone,
        String email,
        String roleTitle,
        String imageUrl,
        String instagramUrl,
        Boolean isActive,
        Set<UUID> branchIds,
        Set<UUID> serviceIds
){}

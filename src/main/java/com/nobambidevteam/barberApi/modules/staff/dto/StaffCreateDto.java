package com.nobambidevteam.barberApi.modules.staff.dto;

import jakarta.validation.constraints.NotNull;

import java.util.Set;
import java.util.UUID;

public record StaffCreateDto (
        @NotNull(message = "El userId es obligatorio")
        UUID userId,
        String firstName,
        String lastName,
        String phone,
        String email,
        String roleTitle,
        String imageUrl,
        String instagramUrl,
        Set<UUID> branchIds,
        Set<UUID> serviceIds
){
    public StaffCreateDto {
        if (branchIds == null) branchIds = Set.of();
        if (serviceIds == null) serviceIds = Set.of();
    }
}

package com.nobambidevteam.barberApi.modules.staff.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

public record StaffDto (
    UUID id,
    UUID userId,
    String firstName,
    String lastName,
    String phone,
    String email,
    String roleTitle,
    String imageUrl,
    String instagramUrl,
    boolean isActive,
    Set<UUID> branchIds,
    Set<UUID>serviceIds
) {}

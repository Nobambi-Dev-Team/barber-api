package com.nobambidevteam.barberApi.modules.staff.mapper;

import com.nobambidevteam.barberApi.modules.branch.entity.BranchEntity;
import com.nobambidevteam.barberApi.modules.service.entity.ServiceEntity;
import com.nobambidevteam.barberApi.modules.staff.dto.StaffCreateDto;
import com.nobambidevteam.barberApi.modules.staff.dto.StaffDto;
import com.nobambidevteam.barberApi.modules.staff.entity.StaffEntity;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class StaffMapper {

    private StaffMapper() {
        throw new IllegalStateException("Clase de utilidad");
    }

    public static StaffEntity toEntity(StaffCreateDto request, Set<BranchEntity> branches, Set<ServiceEntity> services) {
        StaffEntity entity = new StaffEntity();

        entity.setUserId(request.userId());
        entity.setFirstName(request.firstName() != null ? request.firstName() : "Sin nombre");
        entity.setLastName(request.lastName() != null ? request.lastName() : "Sin apellido");
        entity.setPhone(request.phone());
        entity.setEmail(request.email());

        if (request.roleTitle() != null) {
            entity.setRoleTitle(request.roleTitle());
        }

        entity.setImageUrl(request.imageUrl());
        entity.setInstagramUrl(request.instagramUrl());
        entity.setActive(true);

        entity.setBranches(branches);
        entity.setServices(services);

        return entity;
    }

    public static StaffDto toDto(StaffEntity entity) {
        return new StaffDto(
                entity.getId(),
                entity.getUserId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getPhone(),
                entity.getEmail(),
                entity.getRoleTitle(),
                entity.getImageUrl(),
                entity.getInstagramUrl(),
                entity.isActive(),
                entity.getBranches() != null ?
                        entity.getBranches().stream().map(BranchEntity::getId).collect(Collectors.toSet()) : new HashSet<>(),
                entity.getServices() != null ?
                        entity.getServices().stream().map(ServiceEntity::getId).collect(Collectors.toSet()) : new HashSet<>()
        );
    }
}

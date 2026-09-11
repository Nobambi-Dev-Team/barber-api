package com.nobambidevteam.barberApi.modules.staff.service;

import com.nobambidevteam.barberApi.exceptions.BusinessRuleException;
import com.nobambidevteam.barberApi.modules.branch.entity.BranchEntity;
import com.nobambidevteam.barberApi.modules.branch.repository.IBranchRepository;
import com.nobambidevteam.barberApi.modules.service.entity.ServiceEntity;
import com.nobambidevteam.barberApi.modules.service.repository.IServiceRepository;
import com.nobambidevteam.barberApi.modules.staff.dto.StaffCreateDto;
import com.nobambidevteam.barberApi.modules.staff.dto.StaffDto;
import com.nobambidevteam.barberApi.modules.staff.entity.StaffEntity;
import com.nobambidevteam.barberApi.modules.staff.mapper.StaffMapper;
import com.nobambidevteam.barberApi.modules.staff.repository.IStaffRepository;
import com.nobambidevteam.barberApi.modules.staff.service.interfaces.IStaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static sun.security.jca.GetInstance.getServices;

@Service
@RequiredArgsConstructor
public class StaffService implements IStaffService {

    private final IStaffRepository staffRepository;
    private final IBranchRepository branchRepository;
    private final IServiceRepository serviceRepository;

    @Override
    @Transactional
    public StaffDto save(StaffCreateDto request) {

        // validaciones de negocio
        validateExistsStaff(request.userId());

        // recuperación de relaciones
        Set<BranchEntity> branches = getBranches(request.branchIds());
        Set<ServiceEntity> services = getServices(request.serviceIds());

        // mapeo dto a entidad
        StaffEntity staffToSave = StaffMapper.toEntity(request, branches, services);

        // persistencia
        staffToSave = staffRepository.save(staffToSave);

        // mapeo entidad a dto
        return StaffMapper.toDto(staffToSave);
    }

    private void validateExistsStaff(UUID userId) {
        boolean exists = staffRepository.existsByUserId(userId);
        if (exists) {
            throw new BusinessRuleException("Ya existe un miembro del staff asociado a este usuario.");
        }
    }

    private Set<BranchEntity> getBranches(Set<UUID> branchIds) {
        if (branchIds == null || branchIds.isEmpty()) return new HashSet<>();
        return new HashSet<>(branchRepository.findAllById(branchIds));
    }

    private Set<ServiceEntity> getServices(Set<UUID> serviceIds) {
        if (serviceIds == null || serviceIds.isEmpty()) return new HashSet<>();
        return new HashSet<>(serviceRepository.findAllById(serviceIds));
    }
}


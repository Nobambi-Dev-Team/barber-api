package com.nobambidevteam.barberApi.modules.staff.service;

import com.nobambidevteam.barberApi.exceptions.BusinessRuleException;
import com.nobambidevteam.barberApi.exceptions.ResourceNotFoundException;
import com.nobambidevteam.barberApi.modules.branch.entity.BranchEntity;
import com.nobambidevteam.barberApi.modules.branch.repository.IBranchRepository;
import com.nobambidevteam.barberApi.modules.service.entity.ServiceEntity;
import com.nobambidevteam.barberApi.modules.service.repository.IServiceRepository;
import com.nobambidevteam.barberApi.modules.staff.dto.StaffCreateDto;
import com.nobambidevteam.barberApi.modules.staff.dto.StaffDto;
import com.nobambidevteam.barberApi.modules.staff.dto.StaffUpdateDto;
import com.nobambidevteam.barberApi.modules.staff.entity.StaffEntity;
import com.nobambidevteam.barberApi.modules.staff.mapper.StaffMapper;
import com.nobambidevteam.barberApi.modules.staff.repository.IStaffRepository;
import com.nobambidevteam.barberApi.modules.staff.service.interfaces.IStaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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

    @Override
    public List<StaffDto> getAll() {
        return staffRepository.findAll()
                .stream()
                .map(StaffMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public StaffDto update(UUID id, StaffUpdateDto request) {

        StaffEntity staff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el miembro del staff con id: " + id));

        updateStaffFields(staff, request);

        StaffEntity updatedEntity = staffRepository.save(staff);

        return StaffMapper.toDto(updatedEntity);
    }

    private void updateStaffFields(StaffEntity staff, StaffUpdateDto request) {
        if (request.firstName() != null) {
            staff.setFirstName(request.firstName());
        }

        if (request.lastName() != null) {
            staff.setLastName(request.lastName());
        }

        if (request.phone() != null) {
            staff.setPhone(request.phone());
        }

        if (request.email() != null) {
            staff.setEmail(request.email());
        }

        if (request.roleTitle() != null) {
            staff.setRoleTitle(request.roleTitle());
        }

        if (request.imageUrl() != null) {
            staff.setImageUrl(request.imageUrl());
        }

        if (request.instagramUrl() != null) {
            staff.setInstagramUrl(request.instagramUrl());
        }

        if (request.isActive() != null) {
            staff.setActive(request.isActive());
        }

        if (request.branchIds() != null) {
            staff.setBranches(getBranches(request.branchIds()));
        }

        if (request.serviceIds() != null) {
            staff.setServices(getServices(request.serviceIds()));
        }
    }

    @Override
    public void deleteLogical(UUID id) {
        StaffEntity staff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el staff con el id " + id));

        if (staff.isActive()) {
            staff.setActive(false);
            staffRepository.save(staff);
        }
    }
}



package com.nobambidevteam.barberApi.modules.schedule.service;

import com.nobambidevteam.barberApi.exceptions.BusinessRuleException;
import com.nobambidevteam.barberApi.exceptions.ResourceNotFoundException;
import com.nobambidevteam.barberApi.modules.branch.entity.BranchEntity;
import com.nobambidevteam.barberApi.modules.branch.repository.IBranchRepository;
import com.nobambidevteam.barberApi.modules.schedule.dto.exception.ScheduleExceptionCreateDto;
import com.nobambidevteam.barberApi.modules.schedule.dto.exception.ScheduleExceptionDto;
import com.nobambidevteam.barberApi.modules.schedule.dto.exception.ScheduleExceptionUpdateDto;
import com.nobambidevteam.barberApi.modules.schedule.entity.DateExceptionEntity;
import com.nobambidevteam.barberApi.modules.schedule.mapper.ScheduleExceptionMapper;
import com.nobambidevteam.barberApi.modules.schedule.repository.IDateExceptionRepository;
import com.nobambidevteam.barberApi.modules.schedule.service.interfaz.IScheduleExceptionService;
import com.nobambidevteam.barberApi.modules.staff.repository.IStaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ScheduleExceptionService implements IScheduleExceptionService {

    private final IDateExceptionRepository exceptionRepository;
    private final IStaffRepository staffRepository;
    private final IBranchRepository branchRepository;

    @Override
    @Transactional
    public ScheduleExceptionDto save(ScheduleExceptionCreateDto request) {
        if (!staffRepository.existsById(request.staffId())) {
            throw new ResourceNotFoundException("No se encontró el Staff con id " + request.staffId());
        }

        BranchEntity branch = branchRepository.findById(request.branchId())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la Sucursal con id " + request.branchId()));

        validateExceptionLogic(request.isWorkingOverride(), request.overrideStartTime(), request.overrideEndTime());

        ZoneId zoneId = ZoneId.of(branch.getTimezone());
        DateExceptionEntity entityToSave = ScheduleExceptionMapper.toEntity(request, zoneId);

        entityToSave = exceptionRepository.save(entityToSave);

        return ScheduleExceptionMapper.toDto(entityToSave, zoneId);
    }

    @Override
    public List<ScheduleExceptionDto> getByStaffId(UUID staffId) {
        if (!staffRepository.existsById(staffId)) {
            throw new ResourceNotFoundException("No se encontró el Staff con id " + staffId);
        }

        List<DateExceptionEntity> exceptions = exceptionRepository.findByStaffId(staffId);

        return exceptions.stream().map(entity -> {
            BranchEntity branch = branchRepository.findById(entity.getBranchId())
                    .orElseThrow(() -> new ResourceNotFoundException("Sucursal huérfana"));
            ZoneId zoneId = ZoneId.of(branch.getTimezone());
            return ScheduleExceptionMapper.toDto(entity, zoneId);
        }).toList();
    }

    @Override
    @Transactional
    public ScheduleExceptionDto update(UUID id, ScheduleExceptionUpdateDto request) {
        DateExceptionEntity entity = exceptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la excepción con id " + id));

        BranchEntity branch = branchRepository.findById(entity.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal asociada no encontrada"));

        ZoneId zoneId = ZoneId.of(branch.getTimezone());

        applyPartialUpdates(entity, request, zoneId);

        boolean isOverride = !entity.isUnavailable();
        validateExceptionLogic(isOverride, entity.getStartTime(), entity.getEndTime());

        DateExceptionEntity updatedEntity = exceptionRepository.save(entity);
        return ScheduleExceptionMapper.toDto(updatedEntity, zoneId);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!exceptionRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se encontró la excepción con id " + id);
        }
        exceptionRepository.deleteById(id);
    }

    private void applyPartialUpdates(DateExceptionEntity entity, ScheduleExceptionUpdateDto request, ZoneId zoneId) {
        // Lógica cuidadosa para actualizar las fechas preservando la abstracción
        if (request.startDateTime() != null) {
            ZonedDateTime startZdt = request.startDateTime().atZone(zoneId);
            entity.setExceptionDate(startZdt.toLocalDate());
            if (entity.isUnavailable()) {
                entity.setStartTime(startZdt.toLocalTime());
            }
        }

        if (request.endDateTime() != null && entity.isUnavailable()) {
            ZonedDateTime endZdt = request.endDateTime().atZone(zoneId);
            entity.setEndTime(endZdt.toLocalTime());
        }

        if (request.isWorkingOverride() != null) {
            entity.setUnavailable(!request.isWorkingOverride());
        }

        if (request.overrideStartTime() != null && !entity.isUnavailable()) {
            entity.setStartTime(request.overrideStartTime());
        }

        if (request.overrideEndTime() != null && !entity.isUnavailable()) {
            entity.setEndTime(request.overrideEndTime());
        }

        if (request.reason() != null) {
            entity.setReason(request.reason());
        }
    }

    private void validateExceptionLogic(boolean isOverride, java.time.LocalTime startTime, java.time.LocalTime endTime) {
        if (isOverride) {
            if (startTime == null || endTime == null) {
                throw new BusinessRuleException("Si la excepción es un horario laboral, debe indicar las horas (override).");
            }
            if (endTime.isBefore(startTime)) {
                throw new BusinessRuleException("La hora de finalización debe ser posterior a la de inicio.");
            }
        }
    }
}
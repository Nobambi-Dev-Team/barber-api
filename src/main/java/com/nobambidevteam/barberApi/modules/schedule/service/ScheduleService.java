package com.nobambidevteam.barberApi.modules.schedule.service;

import com.nobambidevteam.barberApi.exceptions.BusinessRuleException;
import com.nobambidevteam.barberApi.exceptions.ResourceNotFoundException;
import com.nobambidevteam.barberApi.modules.branch.entity.BranchEntity;
import com.nobambidevteam.barberApi.modules.branch.repository.IBranchRepository;
import com.nobambidevteam.barberApi.modules.schedule.dto.ScheduleCreateDto;
import com.nobambidevteam.barberApi.modules.schedule.dto.ScheduleDto;
import com.nobambidevteam.barberApi.modules.schedule.entity.ScheduleEntity;
import com.nobambidevteam.barberApi.modules.schedule.mapper.ScheduleMapper;
import com.nobambidevteam.barberApi.modules.schedule.repository.IScheduleRepository;
import com.nobambidevteam.barberApi.modules.schedule.service.interfaz.IScheduleService;
import com.nobambidevteam.barberApi.modules.staff.entity.StaffEntity;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ScheduleService implements IScheduleService {

    private final IScheduleRepository scheduleRepository;
    private final IBranchRepository branchRepository;


    //-----------------------Save
    @Override
    @Transactional
    public ScheduleDto save(ScheduleCreateDto request) {

        // Obtener dependencias
        BranchEntity branchEntity = branchRepository.findById(request.branchId())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la sucursal de id " + request.branchId()));

        // MOCK de staff entity
        StaffEntity mockStaff = new StaffEntity();
        mockStaff.setId(request.staffId());

        // Validaciones
        validateTimeRange(request.startTime(), request.endTime());
        validateOverlap(request.staffId(), request.dayOfWeek(), request.startTime(), request.endTime());

        // Mapear y Guardar
        ScheduleEntity scheduleToSave = ScheduleMapper.toEntity(request, mockStaff, branchEntity);
        ScheduleEntity savedSchedule = scheduleRepository.save(scheduleToSave);

        return ScheduleMapper.toDto(savedSchedule);
    }

    private void validateOverlap(UUID staffId, int dayOfWeek, LocalTime startTime, LocalTime endTime) {
        int count = scheduleRepository.countOverlappingSchedules(staffId, dayOfWeek, startTime, endTime);
        if (count > 0) {
            throw new BusinessRuleException("El horario elegido se solapa con otro turno existente del barbero.");
        }
    }

    private void validateTimeRange(LocalTime startTime, LocalTime endTime) {
        if (!endTime.isAfter(startTime)) {
            throw new BusinessRuleException("La hora de finalización debe ser posterior a la hora de inicio.");
        }
    }


}

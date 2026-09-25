package com.nobambidevteam.barberApi.modules.schedule.service.interfaz;

import com.nobambidevteam.barberApi.modules.schedule.dto.exception.ScheduleExceptionCreateDto;
import com.nobambidevteam.barberApi.modules.schedule.dto.exception.ScheduleExceptionDto;
import com.nobambidevteam.barberApi.modules.schedule.dto.exception.ScheduleExceptionUpdateDto;

import java.util.List;
import java.util.UUID;

public interface IScheduleExceptionService {
    ScheduleExceptionDto save(ScheduleExceptionCreateDto request);
    List<ScheduleExceptionDto> getByStaffId(UUID staffId);
    ScheduleExceptionDto update(UUID id, ScheduleExceptionUpdateDto request);
    void delete(UUID id);
}
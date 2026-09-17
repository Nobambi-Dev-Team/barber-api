package com.nobambidevteam.barberApi.modules.schedule.service.interfaz;

import com.nobambidevteam.barberApi.modules.schedule.dto.ScheduleCreateDto;
import com.nobambidevteam.barberApi.modules.schedule.dto.ScheduleDto;
import com.nobambidevteam.barberApi.modules.schedule.dto.ScheduleUpdateDto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface IScheduleService {

    ScheduleDto save(ScheduleCreateDto request);

    List<ScheduleDto> getSchedulesByStaff(UUID staffId);

    List<LocalTime> getAvailability(UUID staffId, UUID serviceId, LocalDate date);

    ScheduleDto update(UUID scheduleId, ScheduleUpdateDto request);
}

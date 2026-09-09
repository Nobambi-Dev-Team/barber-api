package com.nobambidevteam.barberApi.modules.schedule.service.interfaz;

import com.nobambidevteam.barberApi.modules.schedule.dto.ScheduleCreateDto;
import com.nobambidevteam.barberApi.modules.schedule.dto.ScheduleDto;

import java.util.List;
import java.util.UUID;

public interface IScheduleService {

    ScheduleDto save(ScheduleCreateDto request);

    List<ScheduleDto> getSchedulesByStaff(UUID staffId);
}

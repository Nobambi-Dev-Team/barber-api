package com.nobambidevteam.barberApi.modules.schedule.service.interfaz;

import com.nobambidevteam.barberApi.modules.schedule.dto.ScheduleCreateDto;
import com.nobambidevteam.barberApi.modules.schedule.dto.ScheduleDto;

public interface IScheduleService {

    ScheduleDto save(ScheduleCreateDto request);

}

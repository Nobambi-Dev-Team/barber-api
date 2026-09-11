package com.nobambidevteam.barberApi.modules.staff.service.interfaces;

import com.nobambidevteam.barberApi.modules.staff.dto.StaffCreateDto;
import com.nobambidevteam.barberApi.modules.staff.dto.StaffDto;

public interface IStaffService {
    StaffDto save(StaffCreateDto request);
}

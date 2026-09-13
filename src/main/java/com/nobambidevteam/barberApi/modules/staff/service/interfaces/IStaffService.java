package com.nobambidevteam.barberApi.modules.staff.service.interfaces;

import com.nobambidevteam.barberApi.modules.staff.dto.StaffCreateDto;
import com.nobambidevteam.barberApi.modules.staff.dto.StaffDto;
import com.nobambidevteam.barberApi.modules.staff.dto.StaffUpdateDto;

import java.util.List;
import java.util.UUID;

public interface IStaffService {
    StaffDto save(StaffCreateDto request);
    List<StaffDto> getAll();
    StaffDto update(UUID id, StaffUpdateDto request);
    void deleteLogical(UUID id);
}

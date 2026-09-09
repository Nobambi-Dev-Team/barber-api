package com.nobambidevteam.barberApi.modules.schedule.controller;

import com.nobambidevteam.barberApi.modules.schedule.dto.ScheduleCreateDto;
import com.nobambidevteam.barberApi.modules.schedule.dto.ScheduleDto;
import com.nobambidevteam.barberApi.modules.schedule.service.interfaz.IScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final IScheduleService scheduleService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('SCHEDULE_MANAGE_ANY')")
    public ScheduleDto createSchedule(@Valid @RequestBody ScheduleCreateDto request){
        return scheduleService.save(request);
    }

    @GetMapping("/staff/{staffId}")
    @ResponseStatus(HttpStatus.OK)
    public List<ScheduleDto> getSchedulesByStaff(@PathVariable UUID staffId){
        return scheduleService.getSchedulesByStaff(staffId);
    }
}

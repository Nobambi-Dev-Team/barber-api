package com.nobambidevteam.barberApi.modules.schedule.controller;

import com.nobambidevteam.barberApi.modules.schedule.dto.exception.ScheduleExceptionCreateDto;
import com.nobambidevteam.barberApi.modules.schedule.dto.exception.ScheduleExceptionDto;
import com.nobambidevteam.barberApi.modules.schedule.dto.exception.ScheduleExceptionUpdateDto;
import com.nobambidevteam.barberApi.modules.schedule.service.interfaz.IScheduleExceptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/schedule-exceptions")
@RequiredArgsConstructor
public class ScheduleExceptionController {

    private final IScheduleExceptionService exceptionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('SCHEDULE_MANAGE_ANY')")
    public ScheduleExceptionDto createException(@Valid @RequestBody ScheduleExceptionCreateDto request) {
        return exceptionService.save(request);
    }

    @GetMapping("/staff/{staffId}")
    @ResponseStatus(HttpStatus.OK)
    public List<ScheduleExceptionDto> getExceptionsByStaff(@PathVariable UUID staffId) {
        return exceptionService.getByStaffId(staffId);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('SCHEDULE_MANAGE_ANY')")
    public ScheduleExceptionDto updateException(@PathVariable UUID id,
                                                @Valid @RequestBody ScheduleExceptionUpdateDto request) {
        return exceptionService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('SCHEDULE_MANAGE_ANY')")
    public void deleteException(@PathVariable UUID id) {
        exceptionService.delete(id);
    }
}
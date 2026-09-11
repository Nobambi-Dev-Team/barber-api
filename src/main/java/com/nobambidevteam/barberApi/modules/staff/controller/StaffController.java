package com.nobambidevteam.barberApi.modules.staff.controller;

import com.nobambidevteam.barberApi.modules.staff.dto.StaffCreateDto;
import com.nobambidevteam.barberApi.modules.staff.dto.StaffDto;
import com.nobambidevteam.barberApi.modules.staff.service.interfaces.IStaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/staff")
@RequiredArgsConstructor
public class StaffController {

    private final IStaffService staffService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('STAFF_MANAGE')")
    public StaffDto createStaff(@Valid @RequestBody StaffCreateDto request) {
        return staffService.save(request);
    }
}

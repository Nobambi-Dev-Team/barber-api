package com.nobambidevteam.barberApi.modules.staff.controller;

import com.nobambidevteam.barberApi.modules.service.dto.ServiceDto;
import com.nobambidevteam.barberApi.modules.service.dto.ServiceUpdateDto;
import com.nobambidevteam.barberApi.modules.staff.dto.StaffCreateDto;
import com.nobambidevteam.barberApi.modules.staff.dto.StaffDto;
import com.nobambidevteam.barberApi.modules.staff.dto.StaffUpdateDto;
import com.nobambidevteam.barberApi.modules.staff.service.interfaces.IStaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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

    @GetMapping
    public ResponseEntity<List<StaffDto>> getAllStaffs() {
        List<StaffDto> staffs = staffService.getAll();
        if (staffs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(staffs);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('STAFF_MANAGE')")
    public StaffDto updateStaff(@PathVariable UUID id,
                                    @Valid @RequestBody StaffUpdateDto request) {
        return staffService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('STAFF_MANAGE')")
    public void deleteStaff(@PathVariable UUID id) {
        staffService.deleteLogical(id);
    }
}

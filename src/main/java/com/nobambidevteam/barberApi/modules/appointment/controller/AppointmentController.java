package com.nobambidevteam.barberApi.modules.appointment.controller;

import com.nobambidevteam.barberApi.modules.appointment.dto.*;
import com.nobambidevteam.barberApi.modules.appointment.service.interfaces.IAppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final IAppointmentService appointmentService;

    @PostMapping("/book")
    public ResponseEntity<AppointmentDto> bookAppointment(@Valid @RequestBody AppointmentBookDto request) {
        AppointmentDto response = appointmentService.book(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<AppointmentDto> verifyOtp(@Valid @RequestBody OtpVerifyRequestDto request) {
        AppointmentDto response = appointmentService.verifyOtp(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/assign")
    @PreAuthorize("hasAuthority('APPOINTMENTS_CREATE')")
    public ResponseEntity<AppointmentDto> assignAppointment(@Valid @RequestBody AppointmentAssignDto request) {
        AppointmentDto response = appointmentService.assign(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/confirm")
    @PreAuthorize("hasAuthority('APPOINTMENTS_CONFIRM')")
    public ResponseEntity<AppointmentDto> confirmAppointment(@PathVariable UUID id) {
        AppointmentDto response = appointmentService.confirm(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('APPOINTMENTS_CANCEL')")
    public ResponseEntity<AppointmentDto> cancelAppointment(
            @PathVariable UUID id,
            @Valid @RequestBody AppointmentCancelDto request) {
        AppointmentDto response = appointmentService.cancel(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/reschedule")
    @PreAuthorize("hasAuthority('APPOINTMENTS_RESCHEDULE')")
    public ResponseEntity<AppointmentDto> rescheduleAppointment(
            @PathVariable UUID id,
            @Valid @RequestBody AppointmentRescheduleDto request) {
        AppointmentDto response = appointmentService.reschedule(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('APPOINTMENTS_VIEW_ANY')")
    public ResponseEntity<List<AppointmentDto>> getAppointments(
            @RequestParam UUID staffId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end) {

        List<AppointmentDto> response = appointmentService.getAppointmentsByStaffAndDateRange(staffId, start, end);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('APPOINTMENTS_VIEW_ANY')")
    public ResponseEntity<List<AppointmentDto>> getPendingAppointments() {

        List<AppointmentDto> response = appointmentService.getPendingAppointments();
        return ResponseEntity.ok(response);
    }
}

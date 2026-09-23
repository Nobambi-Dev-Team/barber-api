package com.nobambidevteam.barberApi.modules.appointment.controller;

import com.nobambidevteam.barberApi.modules.appointment.dto.AppointmentBookDto;
import com.nobambidevteam.barberApi.modules.appointment.dto.AppointmentDto;
import com.nobambidevteam.barberApi.modules.appointment.dto.OtpVerifyRequestDto;
import com.nobambidevteam.barberApi.modules.appointment.service.interfaces.IAppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

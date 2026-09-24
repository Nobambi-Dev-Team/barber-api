package com.nobambidevteam.barberApi.modules.appointment.service.interfaces;

import com.nobambidevteam.barberApi.modules.appointment.dto.*;

import java.util.UUID;

public interface IAppointmentService {
    AppointmentDto book(AppointmentBookDto request);
    AppointmentDto verifyOtp(OtpVerifyRequestDto request);
    AppointmentDto assign(AppointmentAssignDto request);
    AppointmentDto confirm(UUID id);
    AppointmentDto cancel(UUID id, AppointmentCancelDto request);
    AppointmentDto reschedule(UUID id, AppointmentRescheduleDto request);
}

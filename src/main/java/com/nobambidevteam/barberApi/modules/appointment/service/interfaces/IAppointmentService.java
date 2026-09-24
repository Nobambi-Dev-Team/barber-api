package com.nobambidevteam.barberApi.modules.appointment.service.interfaces;

import com.nobambidevteam.barberApi.modules.appointment.dto.AppointmentAssignDto;
import com.nobambidevteam.barberApi.modules.appointment.dto.AppointmentBookDto;
import com.nobambidevteam.barberApi.modules.appointment.dto.AppointmentDto;
import com.nobambidevteam.barberApi.modules.appointment.dto.OtpVerifyRequestDto;

public interface IAppointmentService {
    AppointmentDto book(AppointmentBookDto request);
    AppointmentDto verifyOtp(OtpVerifyRequestDto request);
    AppointmentDto assign(AppointmentAssignDto request);
}

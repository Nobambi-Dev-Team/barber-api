package com.nobambidevteam.barberApi.modules.appointment.service;

import com.nobambidevteam.barberApi.exceptions.BusinessRuleException;
import com.nobambidevteam.barberApi.exceptions.ResourceNotFoundException;
import com.nobambidevteam.barberApi.modules.appointment.dto.*;
import com.nobambidevteam.barberApi.modules.appointment.entity.AppointmentEntity;
import com.nobambidevteam.barberApi.modules.appointment.entity.OtpVerificationEntity;
import com.nobambidevteam.barberApi.modules.appointment.mapper.AppointmentMapper;
import com.nobambidevteam.barberApi.modules.appointment.repository.IAppointmentRepository;
import com.nobambidevteam.barberApi.modules.appointment.repository.IOtpVerificationRepository;
import com.nobambidevteam.barberApi.modules.appointment.service.interfaces.IAppointmentService;
import com.nobambidevteam.barberApi.modules.service.entity.ServiceEntity;
import com.nobambidevteam.barberApi.modules.service.repository.IServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppointmentService implements IAppointmentService {

    private final IAppointmentRepository appointmentRepository;
    private final IOtpVerificationRepository otpVerificationRepository;
    private final IServiceRepository serviceRepository;
    // TODO: Completar cuando se desarrolle el módulo Customer
    // private final ICustomerService customerService;

    @Override
    @Transactional
    public AppointmentDto book(AppointmentBookDto request) {
        // Obtener el servicio para calcular el endAt basado en su duración
        ServiceEntity service = serviceRepository.findById(request.serviceId())
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado con id: " + request.serviceId()));

        Instant endAt = request.startAt().plus(service.getDurationMinutes(), ChronoUnit.MINUTES);

        // Lógica del cliente (Buscar existente o crear uno nuevo)
        // UUID customerId = customerService.getOrCreateCustomer(request.customerFirstName(), request.customerLastName(), request.customerPhone(), request.customerEmail());
        UUID mockCustomerId = UUID.randomUUID(); // TODO: Reemplazar con lógica real de CustomerService

        // Crear el turno en estado PENDING
        AppointmentEntity appointment = AppointmentMapper.toEntity(request, mockCustomerId, endAt);
        appointment = appointmentRepository.save(appointment);

        // Generar y guardar OTP
        generateAndSaveOtp(request.customerPhone());

        return AppointmentMapper.toDto(appointment);
    }

    @Override
    @Transactional
    public AppointmentDto verifyOtp(OtpVerifyRequestDto request) {
        // Buscar el turno
        AppointmentEntity appointment = appointmentRepository.findById(request.appointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Turno no encontrado"));

        if (!"PENDING".equalsIgnoreCase(appointment.getStatus())) {
            throw new BusinessRuleException("El turno ya no está en estado pendiente.");
        }

        // Obtener el teléfono del cliente asociado al turno
        // String customerPhone = customerService.findById(appointment.getCustomerId()).getPhone();
        String mockCustomerPhone = "+54 11 5555-9999"; // TODO: Reemplazar con lógica real

        // Validar OTP
        validateOtp(mockCustomerPhone, request.code());

        // Confirmar turno
        appointment.setStatus("CONFIRMED");
        appointment = appointmentRepository.save(appointment);

        return AppointmentMapper.toDto(appointment);
    }

    @Override
    @Transactional
    public AppointmentDto assign(AppointmentAssignDto request) {
        // Obtener el servicio para calcular la duración
        ServiceEntity service = serviceRepository.findById(request.serviceId())
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado con id: " + request.serviceId()));

        Instant endAt = request.startAt().plus(service.getDurationMinutes(), ChronoUnit.MINUTES);

        // TODO -> Opcional: Validar que el customerId realmente exista en la base de datos
        // boolean customerExists = customerRepository.existsById(request.customerId());
        // if (!customerExists) throw new ResourceNotFoundException("Cliente no encontrado");

        // Crear el turno directamente en estado CONFIRMED
        AppointmentEntity appointment = AppointmentMapper.toEntity(request, endAt);
        appointment = appointmentRepository.save(appointment);

        return AppointmentMapper.toDto(appointment);
    }

    @Override
    @Transactional
    public AppointmentDto confirm(UUID id) {
        AppointmentEntity appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Turno no encontrado con id: " + id));

        // Regla de negocio: Solo podemos confirmar turnos pendientes
        if (!"PENDING".equalsIgnoreCase(appointment.getStatus())) {
            throw new BusinessRuleException("Solo se pueden confirmar turnos en estado PENDING.");
        }

        appointment.setStatus("CONFIRMED");
        appointment = appointmentRepository.save(appointment);

        return AppointmentMapper.toDto(appointment);
    }

    @Override
    @Transactional
    public AppointmentDto cancel(UUID id, AppointmentCancelDto request) {
        AppointmentEntity appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Turno no encontrado con id: " + id));

        // Regla de negocio: Solo podemos confirmar turnos pendientes
        if ("CANCELLED".equalsIgnoreCase(appointment.getStatus())) {
            throw new BusinessRuleException("El turno ya se encuentra cancelado");
        }

        appointment.setStatus("CANCELLED");
        appointment.setCancelReason(request.reason());
        appointment = appointmentRepository.save(appointment);

        return AppointmentMapper.toDto(appointment);
    }

    @Override
    @Transactional
    public AppointmentDto reschedule(UUID id, AppointmentRescheduleDto request) {
        AppointmentEntity appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Turno no encontrado con id: " + id));

        // Regla de negocio: No aplica a turnos CANCELLED ni COMPLETED
        String status = appointment.getStatus().toUpperCase();
        if ("CANCELLED".equals(status) || "COMPLETED".equals(status)) {
            throw new BusinessRuleException("No se puede reprogramar un turno que ya está " + status + ".");
        }

        // Recuperamos el servicio asociado para saber su duración (necesario para el nuevo endAt)
        ServiceEntity service = serviceRepository.findById(appointment.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Servicio asociado no encontrado"));

        Instant newEndAt = request.startAt().plus(service.getDurationMinutes(), ChronoUnit.MINUTES);

        appointment.setStartAt(request.startAt());
        appointment.setEndAt(newEndAt);
        // TODO preguntar a los chango: Si el turno estaba PENDING y se reprograma por admin, ¿pasa a CONFIRMED o sigue PENDING?
        // En este caso mantenemos su estado original, o podría forzarse a CONFIRMED según requiera el negocio.

        appointment = appointmentRepository.save(appointment);

        return AppointmentMapper.toDto(appointment);
    }

    private void generateAndSaveOtp(String phoneNumber) {
        // Genera un código de 6 dígitos aleatorio
        String code = String.format("%06d", new Random().nextInt(999999));

        OtpVerificationEntity otp = new OtpVerificationEntity();
        otp.setPhoneNumber(phoneNumber);
        otp.setOtpCode(code);
        otp.setExpiresAt(Instant.now().plus(10, ChronoUnit.MINUTES)); // Expira en 10 min
        otp.setVerified(false);

        otpVerificationRepository.save(otp);

        // TODO: Aquí podrías lanzar un evento o llamar a un servicio de SMS/WhatsApp para enviar el código al cliente
        System.out.println("OTP generado para " + phoneNumber + ": " + code);
    }

    private void validateOtp(String phoneNumber, String code) {
        OtpVerificationEntity otp = otpVerificationRepository.findTopByPhoneNumberAndOtpCodeOrderByCreatedAtDesc(phoneNumber, code)
                .orElseThrow(() -> new BusinessRuleException("Código OTP inválido o no existe para este número."));

        if (otp.isVerified()) {
            throw new BusinessRuleException("Este código OTP ya fue utilizado.");
        }

        if (otp.getExpiresAt().isBefore(Instant.now())) {
            throw new BusinessRuleException("El código OTP ha expirado.");
        }

        // Marcar como verificado
        otp.setVerified(true);
        otpVerificationRepository.save(otp);
    }


}

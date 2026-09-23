package com.nobambidevteam.barberApi.modules.appointment.repository;

import com.nobambidevteam.barberApi.modules.appointment.entity.OtpVerificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IOtpVerificationRepository extends JpaRepository<OtpVerificationEntity, UUID> {
    Optional<OtpVerificationEntity> findTopByPhoneNumberAndOtpCodeOrderByCreatedAtDesc(String phoneNumber, String otpCode);
}

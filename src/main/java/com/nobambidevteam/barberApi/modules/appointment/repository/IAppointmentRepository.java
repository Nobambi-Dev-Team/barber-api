package com.nobambidevteam.barberApi.modules.appointment.repository;

import com.nobambidevteam.barberApi.modules.appointment.entity.AppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface IAppointmentRepository extends JpaRepository<AppointmentEntity, UUID> {
    // Busca los turnos de un staff en un rango de fechas y los ordena por hora de inicio
    List<AppointmentEntity> findByStaffIdAndStartAtBetweenOrderByStartAtAsc(UUID staffId, Instant start, Instant end);

    // Busca turnos por estado (ej: "PENDING") y los ordena por hora de inicio
    List<AppointmentEntity> findByStatusOrderByStartAtAsc(String status);
}

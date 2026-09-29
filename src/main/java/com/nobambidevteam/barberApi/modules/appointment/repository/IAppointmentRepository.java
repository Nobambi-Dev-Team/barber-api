package com.nobambidevteam.barberApi.modules.appointment.repository;

import com.nobambidevteam.barberApi.modules.appointment.entity.AppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.nobambidevteam.barberApi.modules.appointment.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface IAppointmentRepository extends JpaRepository<AppointmentEntity, UUID> {

    @Query("SELECT a FROM AppointmentEntity a " +
            "WHERE a.staffId = :staffId " +
            "AND a.status IN ('PENDING', 'CONFIRMED') " +
            "AND a.startAt >= :startOfDay " +
            "AND a.endAt <= :endOfDay")
    List<AppointmentEntity> findActiveAppointmentsByStaffAndDate(@Param("staffId") UUID staffId,
                                                                 @Param("startOfDay") Instant startOfDay,
                                                                 @Param("endOfDay") Instant endOfDay);

    // Busca los turnos de un staff en un rango de fechas y los ordena por hora de inicio
    List<AppointmentEntity> findByStaffIdAndStartAtBetweenOrderByStartAtAsc(UUID staffId, Instant start, Instant end);

    // Busca turnos por estado (ej: "PENDING") y los ordena por hora de inicio
    List<AppointmentEntity> findByStatusOrderByStartAtAsc(AppointmentStatus status);
}

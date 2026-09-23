package com.nobambidevteam.barberApi.modules.appointment.repository;

import com.nobambidevteam.barberApi.modules.appointment.entity.AppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

}

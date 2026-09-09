package com.nobambidevteam.barberApi.modules.schedule.repository;

import com.nobambidevteam.barberApi.modules.schedule.entity.ScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.UUID;

@Repository
public interface IScheduleRepository extends JpaRepository<ScheduleEntity, UUID> {

    @Query("SELECT COUNT(s) FROM ScheduleEntity s " +
            "WHERE s.staff.id = :staffId " +
            "AND s.dayOfWeek = :dayOfWeek " +
            "AND s.startTime < :endTime " +
            "AND s.endTime > :startTime")
    int countOverlappingSchedules(@Param("staffId") UUID staffId,
                                  @Param("dayOfWeek") int dayOfWeek,
                                  @Param("startTime") LocalTime startTime,
                                  @Param("endTime") LocalTime endTime);
}

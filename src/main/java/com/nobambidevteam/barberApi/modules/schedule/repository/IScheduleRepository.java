package com.nobambidevteam.barberApi.modules.schedule.repository;

import com.nobambidevteam.barberApi.modules.schedule.entity.ScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
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


    @Query("SELECT COUNT(s) FROM ScheduleEntity s " +
            "WHERE s.id != :scheduleId " +
            "AND s.staff.id = :staffId " +
            "AND s.dayOfWeek = :dayOfWeek " +
            "AND s.startTime < :endTime " +
            "AND s.endTime > :startTime " +
            "AND s.isActive = true")
    int countOverlappingSchedulesForUpdate(@Param("scheduleId") UUID scheduleId,
                                           @Param("staffId") UUID staffId,
                                           @Param("dayOfWeek") int dayOfWeek,
                                           @Param("startTime") LocalTime startTime,
                                           @Param("endTime") LocalTime endTime);

    @Query("SELECT s FROM ScheduleEntity s " +
            "WHERE s.staff.id = :staffId " +
            "AND s.isActive = true " +
            "ORDER BY " +
            "CASE WHEN s.dayOfWeek = 0 THEN 7 ELSE s.dayOfWeek END ASC, s.startTime ASC")
    List<ScheduleEntity> findActiveSchedulesByStaff(@Param("staffId") UUID staffId);


    @Query("""
        SELECT s FROM ScheduleEntity s 
        WHERE s.staff.id = :staffId 
          AND s.dayOfWeek = :dayOfWeek 
          AND s.isActive = true 
        ORDER BY s.startTime ASC
    """)
    List<ScheduleEntity> findSchedulesForAvailability(
            @Param("staffId") UUID staffId,
            @Param("dayOfWeek") int dayOfWeek);


}

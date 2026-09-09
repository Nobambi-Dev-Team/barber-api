package com.nobambidevteam.barberApi.modules.schedule.repository;

import com.nobambidevteam.barberApi.modules.schedule.entity.ScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IScheduleRepository extends JpaRepository<ScheduleEntity, UUID> {
}

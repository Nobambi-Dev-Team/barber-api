package com.nobambidevteam.barberApi.modules.appointment.repository;

import com.nobambidevteam.barberApi.modules.appointment.entity.AppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IAppointmentRepository extends JpaRepository<AppointmentEntity, UUID> {

}

package com.nobambidevteam.barberApi.modules.appointment.mapper;

import com.nobambidevteam.barberApi.modules.appointment.dto.AppointmentAssignDto;
import com.nobambidevteam.barberApi.modules.appointment.dto.AppointmentBookDto;
import com.nobambidevteam.barberApi.modules.appointment.dto.AppointmentDto;
import com.nobambidevteam.barberApi.modules.appointment.entity.AppointmentEntity;

import java.time.Instant;
import java.util.UUID;

public class AppointmentMapper {

    private AppointmentMapper() {
        throw new IllegalStateException("Clase de utilidad");
    }

    public static AppointmentEntity toEntity(AppointmentBookDto request, UUID customerId, Instant endAt) {
        AppointmentEntity entity = new AppointmentEntity();
        entity.setBranchId(request.branchId());
        entity.setStaffId(request.staffId());
        entity.setServiceId(request.serviceId());
        entity.setCustomerId(customerId);
        entity.setStartAt(request.startAt());
        entity.setEndAt(endAt);
        entity.setStatus("PENDING");
        entity.setNotes(request.notes());
        return entity;
    }

    public static AppointmentEntity toEntity(AppointmentAssignDto request, Instant endAt) {
        AppointmentEntity entity = new AppointmentEntity();
        entity.setBranchId(request.branchId());
        entity.setStaffId(request.staffId());
        entity.setServiceId(request.serviceId());
        entity.setCustomerId(request.customerId());
        entity.setStartAt(request.startAt());
        entity.setEndAt(endAt);
        entity.setStatus("CONFIRMED"); // Directo a confirmado
        entity.setNotes(request.notes());
        return entity;
    }

    public static AppointmentDto toDto(AppointmentEntity entity) {
        return new AppointmentDto(
                entity.getId(),
                entity.getBranchId(),
                entity.getStaffId(),
                entity.getServiceId(),
                entity.getCustomerId(),
                entity.getStartAt(),
                entity.getEndAt(),
                entity.getStatus(),
                entity.getNotes(),
                entity.getCancelReason()
        );
    }


}

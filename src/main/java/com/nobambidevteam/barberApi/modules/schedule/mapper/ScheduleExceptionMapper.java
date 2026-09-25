package com.nobambidevteam.barberApi.modules.schedule.mapper;

import com.nobambidevteam.barberApi.modules.schedule.dto.exception.ScheduleExceptionCreateDto;
import com.nobambidevteam.barberApi.modules.schedule.dto.exception.ScheduleExceptionDto;
import com.nobambidevteam.barberApi.modules.schedule.entity.DateExceptionEntity;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class ScheduleExceptionMapper {

    private ScheduleExceptionMapper() {
        throw new IllegalStateException("Clase de utilidad");
    }

    public static DateExceptionEntity toEntity(ScheduleExceptionCreateDto dto, ZoneId branchZoneId) {
        if (dto == null) return null;

        DateExceptionEntity entity = new DateExceptionEntity();
        entity.setStaffId(dto.staffId());
        entity.setBranchId(dto.branchId());
        entity.setReason(dto.reason());

        // Traducimos el UTC a la hora local de la sucursal (ej: Santiago del Estero / Buenos Aires)
        ZonedDateTime startZdt = dto.startDateTime().atZone(branchZoneId);
        ZonedDateTime endZdt = dto.endDateTime().atZone(branchZoneId);

        entity.setExceptionDate(startZdt.toLocalDate());

        if (Boolean.FALSE.equals(dto.isWorkingOverride())) {
            // Es un Bloqueo / Feriado
            entity.setUnavailable(true);
            entity.setStartTime(startZdt.toLocalTime());
            entity.setEndTime(endZdt.toLocalTime());
        } else {
            // Es un Reemplazo Laboral (trabaja un día que no le tocaba)
            entity.setUnavailable(false);
            entity.setStartTime(dto.overrideStartTime());
            entity.setEndTime(dto.overrideEndTime());
        }

        return entity;
    }

    public static ScheduleExceptionDto toDto(DateExceptionEntity entity, ZoneId branchZoneId) {
        if (entity == null) return null;

        boolean isWorkingOverride = !entity.isUnavailable();

        // Reconstruimos el Instant (UTC) para que el frontend lo entienda sin problemas
        ZonedDateTime startZdt = entity.getExceptionDate().atTime(entity.getStartTime()).atZone(branchZoneId);
        ZonedDateTime endZdt = entity.getExceptionDate().atTime(entity.getEndTime()).atZone(branchZoneId);

        return new ScheduleExceptionDto(
                entity.getId(),
                entity.getStaffId(),
                entity.getBranchId(),
                startZdt.toInstant(),
                endZdt.toInstant(),
                isWorkingOverride,
                isWorkingOverride ? entity.getStartTime() : null,
                isWorkingOverride ? entity.getEndTime() : null,
                entity.getReason()
        );
    }
}
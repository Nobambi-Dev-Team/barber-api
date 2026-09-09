package com.nobambidevteam.barberApi.modules.schedule.mapper;

import com.nobambidevteam.barberApi.modules.branch.entity.BranchEntity;
import com.nobambidevteam.barberApi.modules.schedule.dto.ScheduleCreateDto;
import com.nobambidevteam.barberApi.modules.schedule.dto.ScheduleDto;
import com.nobambidevteam.barberApi.modules.schedule.entity.ScheduleEntity;
import com.nobambidevteam.barberApi.modules.staff.entity.StaffEntity;

public class ScheduleMapper {

    private ScheduleMapper() {
        throw new IllegalStateException("Clase de utilidad");
    }

    // create DTO to Entity
    public static ScheduleEntity toEntity(ScheduleCreateDto dto, StaffEntity staffEntity, BranchEntity branchEntity) {
        if (dto == null) return null;

        return ScheduleEntity.builder()
                .staff(staffEntity)
                .branch(branchEntity)
                .dayOfWeek(dto.dayOfWeek())
                .startTime(dto.startTime())
                .endTime(dto.endTime())
                .isActive(true)
                .build();

    }

    // Entity to DTO
    public static ScheduleDto toDto(ScheduleEntity entity) {
        if (entity == null) return null;

        return new ScheduleDto(
                entity.getId(),
                entity.getStaff().getId(),
                entity.getBranch().getId(),
                entity.getDayOfWeek(),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.isActive()
        );

    }

}

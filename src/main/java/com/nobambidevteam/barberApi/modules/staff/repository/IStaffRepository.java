package com.nobambidevteam.barberApi.modules.staff.repository;

import com.nobambidevteam.barberApi.modules.staff.entity.StaffEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IStaffRepository extends JpaRepository<StaffEntity, Long> {
    boolean existsByUserId(UUID userId);
}

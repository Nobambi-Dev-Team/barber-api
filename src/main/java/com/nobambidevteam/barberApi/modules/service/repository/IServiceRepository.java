package com.nobambidevteam.barberApi.modules.service.repository;

import com.nobambidevteam.barberApi.modules.service.entity.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IServiceRepository extends JpaRepository<ServiceEntity, UUID> {

    boolean existsByNameIgnoreCase(String name);

    List<ServiceEntity> findByIsActiveTrue();
}

package com.nobambidevteam.barberApi.modules.branch.repository;

import com.nobambidevteam.barberApi.modules.branch.entity.BranchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IBranchRepository extends JpaRepository<BranchEntity, UUID> {

    boolean existsByNameIgnoreCase(String name);

}

package com.nobambidevteam.barberApi.modules.schedule.repository;

import com.nobambidevteam.barberApi.modules.schedule.entity.DateExceptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface IDateExceptionRepository extends JpaRepository<DateExceptionEntity, UUID> {

    /**
     * Lista las exceptions filtrando por staffId y exceptionDate
     * @param staffId filtra las exception de este staff
     * @param exceptionDate filtra las exception de esta fecha
     * @return
     */
    List<DateExceptionEntity> findByStaffIdAndExceptionDate(UUID staffId, LocalDate exceptionDate);

}

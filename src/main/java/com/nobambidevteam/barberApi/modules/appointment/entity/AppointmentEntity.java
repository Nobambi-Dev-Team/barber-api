package com.nobambidevteam.barberApi.modules.appointment.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "appointments")
@Getter
@Setter
public class AppointmentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "branch_id", nullable = false)
    private UUID branchId;

    @Column(name = "staff_id", nullable = false)
    private UUID staffId;

    @Column(name = "service_id", nullable = false)
    private UUID serviceId;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "start_at", nullable = false)
    private Instant startAt;

    @Column(name = "end_at", nullable = false)
    private Instant endAt;

    @Column(nullable = false)
    private String status = "PENDING";

    private String notes;

    @Column(name = "cancel_reason")
    private String cancelReason;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private Instant updatedAt;
}

package com.nobambidevteam.barberApi.modules.staff.entity;

import com.nobambidevteam.barberApi.modules.branch.entity.BranchEntity;
import com.nobambidevteam.barberApi.modules.service.entity.ServiceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "staff")
@Getter
@Setter
public class StaffEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", unique = true)
    private UUID userId;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    private String phone;
    private String email;

    @Column(name = "role_title")
    private String roleTitle = "Barbero";

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "instagram_url")
    private String instagramUrl;

    @Column(name = "is_active")
    private boolean isActive = true;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "staff_branches",
            joinColumns = @JoinColumn(name = "staff_id"),
            inverseJoinColumns = @JoinColumn(name = "branch_id")
    )
    private Set<BranchEntity> branches;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "staff_services",
            joinColumns = @JoinColumn(name = "staff_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private Set<ServiceEntity> services;
}

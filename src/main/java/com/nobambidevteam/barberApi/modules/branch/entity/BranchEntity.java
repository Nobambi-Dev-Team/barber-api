package com.nobambidevteam.barberApi.modules.branch.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "branches")
@Getter
@Setter
public class BranchEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String address;
    private String phone;
    private String timezone;

    @Column(name = "map_iframe_url")
    private String mapIframeUrl;

    @Column(name = "google_maps_url")
    private String googleMapsUrl;

    @Column(name = "is_active")
    private boolean isActive = true;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private Instant updatedAt;
}

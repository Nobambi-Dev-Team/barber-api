package com.nobambidevteam.barberApi.modules.branch.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "branches")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BranchEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String address;
    private String phone;

    @Column(name = "timezone")
    @Builder.Default
    private String timezone = "America/Argentina/Buenos_Aires";

    @Column(name = "map_iframe_url")
    private String mapIframeUrl;

    @Column(name = "google_maps_url")
    private String googleMapsUrl;

    @Column(name = "is_active")
    @Builder.Default
    private boolean isActive = true;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private Instant updatedAt;
}

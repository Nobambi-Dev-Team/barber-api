package com.nobambidevteam.barberApi.modules.security.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "permissions")
@Getter
@Setter
public class PermissionEntity {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String code;

    private String description;
}

package com.juneth.ota_updates_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "chip_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChipType {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @Column(name = "flash_size")
    private Long flashSize;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "chipType", cascade = CascadeType.PERSIST)
    private List<Device> devices = new ArrayList<>();

    @OneToMany(mappedBy = "chipType", cascade = CascadeType.PERSIST)
    private List<FirmwareImage> firmwareImages = new ArrayList<>();
}

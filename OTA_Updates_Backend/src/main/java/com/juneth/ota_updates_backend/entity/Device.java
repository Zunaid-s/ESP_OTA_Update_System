package com.juneth.ota_updates_backend.entity;

import com.juneth.ota_updates_backend.enums.DeviceStatus;
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
@Table(name = "devices")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "chip_type_id", nullable = false)
    private ChipType chipType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private DeviceGroup deviceGroup;

    @Column(nullable = false, unique = true)
    private String apiKey;

    private String currentFirmwareVersion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeviceStatus status;

    private LocalDateTime lastSeenAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "device", cascade = CascadeType.PERSIST)
    private List<UpdateAssignment> updateAssignments = new ArrayList<>();

    @OneToMany(mappedBy = "device", cascade = CascadeType.PERSIST)
    private List<UpdateHistory> updateHistories = new ArrayList<>();
}

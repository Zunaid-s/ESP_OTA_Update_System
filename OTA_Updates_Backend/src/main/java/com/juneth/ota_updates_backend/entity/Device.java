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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    @Column(nullable = false, unique = true, length = 17)
    private String macAddress;

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

    @ManyToMany
    @JoinTable(
            name = "device_group_memberships",
            joinColumns = @JoinColumn(name = "device_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    @Builder.Default
    private Set<DeviceGroup> deviceGroups = new HashSet<>();

    @OneToMany(mappedBy = "device", cascade = CascadeType.PERSIST)
    @Builder.Default
    private List<UpdateAssignment> updateAssignments = new ArrayList<>();

    @OneToMany(mappedBy = "device", cascade = CascadeType.PERSIST)
    @Builder.Default
    private List<UpdateHistory> updateHistories = new ArrayList<>();
}

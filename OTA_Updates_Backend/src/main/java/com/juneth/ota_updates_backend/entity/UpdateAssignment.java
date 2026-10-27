package com.juneth.ota_updates_backend.entity;

import com.juneth.ota_updates_backend.enums.UpdateAssignmentStatus;
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
@Table(name = "update_assignments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id")
    private Device device;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private DeviceGroup deviceGroup;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "firmware_id", nullable = false)
    private FirmwareImage firmwareImage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UpdateAssignmentStatus status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "updateAssignment", cascade = CascadeType.PERSIST)
    @Builder.Default
    private List<UpdateHistory> updateHistories = new ArrayList<>();
}

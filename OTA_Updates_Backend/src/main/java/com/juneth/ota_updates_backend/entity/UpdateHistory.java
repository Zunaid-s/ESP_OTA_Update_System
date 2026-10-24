package com.juneth.ota_updates_backend.entity;

import com.juneth.ota_updates_backend.enums.UpdateHistoryStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "update_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id")
    private UpdateAssignment updateAssignment;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "firmware_id", nullable = false)
    private FirmwareImage firmwareImage;

    private String previousVersion;

    private String newVersion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UpdateHistoryStatus status;

    private String errorMessage;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}

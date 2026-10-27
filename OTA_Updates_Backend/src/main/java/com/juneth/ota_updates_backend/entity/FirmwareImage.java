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
@Table(name = "firmware_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FirmwareImage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String version;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "chip_type_id", nullable = false)
    private ChipType chipType;

    private String fileName;

    private String filePath;

    private Long fileSize;

    private String checksum;

    @Column(nullable = false)
    private String signature;

    @Column(nullable = false)
    private Boolean isGeneric;

    private String description;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "firmwareImage", cascade = CascadeType.PERSIST)
    @Builder.Default
    private List<UpdateAssignment> updateAssignments = new ArrayList<>();

    @OneToMany(mappedBy = "firmwareImage", cascade = CascadeType.PERSIST)
    @Builder.Default
    private List<UpdateHistory> updateHistories = new ArrayList<>();
}

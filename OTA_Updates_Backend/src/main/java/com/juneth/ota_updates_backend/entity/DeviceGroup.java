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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "device_groups")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @ManyToMany(mappedBy = "deviceGroups")
    @Builder.Default
    private Set<Device> devices = new HashSet<>();

    @OneToMany(mappedBy = "deviceGroup", cascade = CascadeType.PERSIST)
    @Builder.Default
    private List<UpdateAssignment> updateAssignments = new ArrayList<>();
}

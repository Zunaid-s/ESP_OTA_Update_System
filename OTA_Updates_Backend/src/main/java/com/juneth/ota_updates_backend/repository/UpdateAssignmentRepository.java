package com.juneth.ota_updates_backend.repository;

import com.juneth.ota_updates_backend.entity.UpdateAssignment;
import com.juneth.ota_updates_backend.enums.UpdateAssignmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UpdateAssignmentRepository extends JpaRepository<UpdateAssignment, UUID> {
    List<UpdateAssignment> findByDeviceId(UUID deviceId);

    List<UpdateAssignment> findByDeviceGroupId(UUID groupId);

    List<UpdateAssignment> findByDeviceIdAndStatus(UUID deviceId, UpdateAssignmentStatus status);

    List<UpdateAssignment> findByDeviceGroupIdAndStatus(UUID groupId, UpdateAssignmentStatus status);

    List<UpdateAssignment> findByFirmwareImageId(UUID firmwareImageId);

    Optional<UpdateAssignment> findFirstByDeviceIdAndStatusOrderByCreatedAtDesc(UUID deviceId, UpdateAssignmentStatus status);

    Optional<UpdateAssignment> findFirstByDeviceGroupIdAndStatusOrderByCreatedAtDesc(UUID groupId, UpdateAssignmentStatus status);

    @Query("""
        SELECT ua FROM UpdateAssignment ua
        LEFT JOIN ua.device d
        LEFT JOIN ua.deviceGroup dg
        LEFT JOIN dg.devices dgm
        WHERE (d.id = :deviceId OR dgm.id = :deviceId)
          AND ua.status = :status
        ORDER BY ua.createdAt DESC
    """)
    List<UpdateAssignment> findActiveAssignmentsForDevice(
            @Param("deviceId") UUID deviceId,
            @Param("status") UpdateAssignmentStatus status
    );
}

package com.juneth.ota_updates_backend.repository;

import com.juneth.ota_updates_backend.entity.UpdateHistory;
import com.juneth.ota_updates_backend.enums.UpdateHistoryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UpdateHistoryRepository extends JpaRepository<UpdateHistory, UUID> {
    List<UpdateHistory> findByDeviceIdOrderByCreatedAtDesc(UUID deviceId);

    List<UpdateHistory> findByUpdateAssignmentIdOrderByCreatedAtDesc(UUID assignmentId);

    List<UpdateHistory> findByDeviceIdAndStatus(UUID deviceId, UpdateHistoryStatus status);

    Optional<UpdateHistory> findFirstByDeviceIdOrderByCreatedAtDesc(UUID deviceId);
}

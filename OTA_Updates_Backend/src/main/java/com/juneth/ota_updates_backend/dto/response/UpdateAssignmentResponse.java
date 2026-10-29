package com.juneth.ota_updates_backend.dto.response;

import com.juneth.ota_updates_backend.enums.UpdateAssignmentStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record UpdateAssignmentResponse(
        UUID id,
        UUID deviceId,
        String deviceName,
        UUID groupId,
        String groupName,
        UUID firmwareImageId,
        String firmwareVersion,
        String chipTypeName,
        UpdateAssignmentStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}

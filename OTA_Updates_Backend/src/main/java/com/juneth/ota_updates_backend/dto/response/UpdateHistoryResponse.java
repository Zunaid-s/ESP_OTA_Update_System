package com.juneth.ota_updates_backend.dto.response;

import com.juneth.ota_updates_backend.enums.UpdateHistoryStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record UpdateHistoryResponse(
        UUID id,
        UUID deviceId,
        String deviceName,
        UUID assignmentId,
        UUID firmwareImageId,
        String firmwareVersion,
        String previousVersion,
        String newVersion,
        UpdateHistoryStatus status,
        String errorMessage,
        LocalDateTime startedAt,
        LocalDateTime completedAt,
        LocalDateTime createdAt
) {}

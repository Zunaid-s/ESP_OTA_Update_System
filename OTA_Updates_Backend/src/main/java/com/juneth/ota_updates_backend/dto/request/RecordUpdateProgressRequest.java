package com.juneth.ota_updates_backend.dto.request;

import com.juneth.ota_updates_backend.enums.UpdateHistoryStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record RecordUpdateProgressRequest(
        @NotNull(message = "Device ID is required")
        UUID deviceId,

        UUID assignmentId,

        @NotNull(message = "Firmware image ID is required")
        UUID firmwareImageId,

        String previousVersion,

        String newVersion,

        @NotNull(message = "Status is required")
        UpdateHistoryStatus status,

        String errorMessage
) {}

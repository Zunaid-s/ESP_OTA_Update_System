package com.juneth.ota_updates_backend.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateUpdateAssignmentRequest(
        UUID deviceId,

        UUID groupId,

        @NotNull(message = "Firmware image ID is required")
        UUID firmwareImageId
) {
    @AssertTrue(message = "Either deviceId or groupId must be provided, but not both")
    public boolean isValidTarget() {
        return (deviceId != null && groupId == null) || (deviceId == null && groupId != null);
    }
}

package com.juneth.ota_updates_backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Set;
import java.util.UUID;

@Builder
public record AssignDeviceGroupsRequest(
        @NotNull(message = "Group IDs set cannot be null")
        Set<UUID> groupIds
) {}

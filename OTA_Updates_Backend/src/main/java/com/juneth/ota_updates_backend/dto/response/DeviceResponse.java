package com.juneth.ota_updates_backend.dto.response;

import com.juneth.ota_updates_backend.enums.DeviceStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Builder
public record DeviceResponse(
        UUID id,
        String name,
        UUID chipTypeId,
        String chipTypeName,
        String macAddress,
        String apiKey,
        String currentFirmwareVersion,
        DeviceStatus status,
        LocalDateTime lastSeenAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Set<DeviceGroupSummaryResponse> groups
) {}

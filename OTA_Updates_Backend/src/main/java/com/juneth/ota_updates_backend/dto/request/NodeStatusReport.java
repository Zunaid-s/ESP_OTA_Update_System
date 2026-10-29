package com.juneth.ota_updates_backend.dto.request;

import com.juneth.ota_updates_backend.enums.DeviceStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record NodeStatusReport(
        UUID deviceId,
        String macAddress,
        String currentFirmwareVersion,
        DeviceStatus status,
        Integer freeHeap,
        Integer rssi
) {}

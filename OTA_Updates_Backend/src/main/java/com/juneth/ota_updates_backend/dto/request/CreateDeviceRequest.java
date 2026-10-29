package com.juneth.ota_updates_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.Set;
import java.util.UUID;

@Builder
public record CreateDeviceRequest(
        @NotBlank(message = "Device name is required")
        @Size(max = 255, message = "Device name must not exceed 255 characters")
        String name,

        @NotNull(message = "Chip type ID is required")
        UUID chipTypeId,

        @NotBlank(message = "MAC address is required")
        @Pattern(
                regexp = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$",
                message = "MAC address must be in format XX:XX:XX:XX:XX:XX or XX-XX-XX-XX-XX-XX"
        )
        String macAddress,

        Set<UUID> groupIds
) {}

package com.juneth.ota_updates_backend.dto.request;

import com.juneth.ota_updates_backend.enums.DeviceStatus;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UpdateDeviceRequest(
        @Size(max = 255, message = "Device name must not exceed 255 characters")
        String name,

        DeviceStatus status
) {}

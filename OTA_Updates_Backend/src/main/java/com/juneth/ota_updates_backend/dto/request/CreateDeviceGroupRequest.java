package com.juneth.ota_updates_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CreateDeviceGroupRequest(
        @NotBlank(message = "Group name is required")
        @Size(max = 255, message = "Group name must not exceed 255 characters")
        String name,

        String description
) {}

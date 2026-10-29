package com.juneth.ota_updates_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UploadFirmwareMetadataRequest(
        @NotBlank(message = "Firmware version is required")
        @Size(max = 100, message = "Version must not exceed 100 characters")
        String version,

        @NotNull(message = "Chip type ID is required")
        UUID chipTypeId,

        @NotBlank(message = "Firmware signature is mandatory - unsigned firmware is rejected by nodes")
        String signature,

        Boolean isGeneric,

        String description
) {}

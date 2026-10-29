package com.juneth.ota_updates_backend.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record FirmwareImageResponse(
        UUID id,
        String version,
        UUID chipTypeId,
        String chipTypeName,
        String fileName,
        Long fileSize,
        String checksum,
        String signature,
        Boolean isGeneric,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}

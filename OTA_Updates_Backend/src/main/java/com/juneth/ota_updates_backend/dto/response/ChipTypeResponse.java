package com.juneth.ota_updates_backend.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ChipTypeResponse(
        UUID id,
        String name,
        String description,
        Integer flashSize,
        LocalDateTime createdAt,
        int deviceCount
) {}

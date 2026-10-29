package com.juneth.ota_updates_backend.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record DeviceGroupSummaryResponse(
        UUID id,
        String name
) {}

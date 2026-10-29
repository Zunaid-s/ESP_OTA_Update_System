package com.juneth.ota_updates_backend.dto.request;

import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record UpdateChipTypeRequest(
        String description,

        @Positive(message = "Flash size must be greater than 0")
        Integer flashSize
) {}

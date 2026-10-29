package com.juneth.ota_updates_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CreateChipTypeRequest(
        @NotBlank(message = "Chip type name is required")
        @Size(max = 50, message = "Name must not exceed 50 characters")
        String name,

        String description,

        @NotNull(message = "Flash size is required")
        @Positive(message = "Flash size must be greater than 0")
        Integer flashSize
) {}

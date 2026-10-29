package com.juneth.ota_updates_backend.mapper;

import com.juneth.ota_updates_backend.dto.request.CreateChipTypeRequest;
import com.juneth.ota_updates_backend.dto.request.UpdateChipTypeRequest;
import com.juneth.ota_updates_backend.dto.response.ChipTypeResponse;
import com.juneth.ota_updates_backend.entity.ChipType;
import org.springframework.stereotype.Component;

@Component
public class ChipTypeMapper {

    public ChipType toEntity(CreateChipTypeRequest request) {
        if (request == null) {
            return null;
        }
        return ChipType.builder()
                .name(request.name())
                .description(request.description())
                .flashSize(request.flashSize())
                .build();
    }

    public void updateEntity(ChipType chipType, UpdateChipTypeRequest request) {
        if (chipType == null || request == null) {
            return;
        }
        if (request.description() != null) {
            chipType.setDescription(request.description());
        }
        if (request.flashSize() != null) {
            chipType.setFlashSize(request.flashSize());
        }
    }

    public ChipTypeResponse toResponse(ChipType chipType) {
        if (chipType == null) {
            return null;
        }
        return ChipTypeResponse.builder()
                .id(chipType.getId())
                .name(chipType.getName())
                .description(chipType.getDescription())
                .flashSize(chipType.getFlashSize())
                .createdAt(chipType.getCreatedAt())
                .deviceCount(chipType.getDevices() != null ? chipType.getDevices().size() : 0)
                .build();
    }
}

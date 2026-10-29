package com.juneth.ota_updates_backend.mapper;

import com.juneth.ota_updates_backend.dto.request.CreateDeviceGroupRequest;
import com.juneth.ota_updates_backend.dto.request.UpdateDeviceGroupRequest;
import com.juneth.ota_updates_backend.dto.response.DeviceGroupResponse;
import com.juneth.ota_updates_backend.dto.response.DeviceGroupSummaryResponse;
import com.juneth.ota_updates_backend.entity.DeviceGroup;
import org.springframework.stereotype.Component;

@Component
public class DeviceGroupMapper {

    public DeviceGroup toEntity(CreateDeviceGroupRequest request) {
        if (request == null) {
            return null;
        }
        return DeviceGroup.builder()
                .name(request.name())
                .description(request.description())
                .build();
    }

    public void updateEntity(DeviceGroup group, UpdateDeviceGroupRequest request) {
        if (group == null || request == null) {
            return;
        }
        if (request.name() != null && !request.name().isBlank()) {
            group.setName(request.name());
        }
        if (request.description() != null) {
            group.setDescription(request.description());
        }
    }

    public DeviceGroupResponse toResponse(DeviceGroup group) {
        if (group == null) {
            return null;
        }
        return DeviceGroupResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .createdAt(group.getCreatedAt())
                .updatedAt(group.getUpdatedAt())
                .deviceCount(group.getDevices() != null ? group.getDevices().size() : 0)
                .build();
    }

    public DeviceGroupSummaryResponse toSummary(DeviceGroup group) {
        if (group == null) {
            return null;
        }
        return DeviceGroupSummaryResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .build();
    }
}

package com.juneth.ota_updates_backend.mapper;

import com.juneth.ota_updates_backend.dto.request.CreateDeviceRequest;
import com.juneth.ota_updates_backend.dto.response.DeviceGroupSummaryResponse;
import com.juneth.ota_updates_backend.dto.response.DeviceResponse;
import com.juneth.ota_updates_backend.entity.ChipType;
import com.juneth.ota_updates_backend.entity.Device;
import com.juneth.ota_updates_backend.entity.DeviceGroup;
import com.juneth.ota_updates_backend.enums.DeviceStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DeviceMapper {

    private final DeviceGroupMapper deviceGroupMapper;

    public Device toEntity(CreateDeviceRequest request, ChipType chipType, Set<DeviceGroup> groups, String apiKey) {
        if (request == null) {
            return null;
        }
        return Device.builder()
                .name(request.name())
                .chipType(chipType)
                .macAddress(request.macAddress().toUpperCase())
                .apiKey(apiKey)
                .status(DeviceStatus.PAIRED)
                .deviceGroups(groups != null ? groups : Collections.emptySet())
                .build();
    }

    public DeviceResponse toResponse(Device device) {
        if (device == null) {
            return null;
        }
        Set<DeviceGroupSummaryResponse> groupSummaries = device.getDeviceGroups() != null
                ? device.getDeviceGroups().stream()
                .map(deviceGroupMapper::toSummary)
                .collect(Collectors.toSet())
                : Collections.emptySet();

        return DeviceResponse.builder()
                .id(device.getId())
                .name(device.getName())
                .chipTypeId(device.getChipType() != null ? device.getChipType().getId() : null)
                .chipTypeName(device.getChipType() != null ? device.getChipType().getName() : null)
                .macAddress(device.getMacAddress())
                .apiKey(device.getApiKey())
                .currentFirmwareVersion(device.getCurrentFirmwareVersion())
                .status(device.getStatus())
                .lastSeenAt(device.getLastSeenAt())
                .createdAt(device.getCreatedAt())
                .updatedAt(device.getUpdatedAt())
                .groups(groupSummaries)
                .build();
    }
}

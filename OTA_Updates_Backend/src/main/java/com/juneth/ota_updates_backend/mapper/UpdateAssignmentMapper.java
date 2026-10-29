package com.juneth.ota_updates_backend.mapper;

import com.juneth.ota_updates_backend.dto.response.UpdateAssignmentResponse;
import com.juneth.ota_updates_backend.entity.Device;
import com.juneth.ota_updates_backend.entity.DeviceGroup;
import com.juneth.ota_updates_backend.entity.FirmwareImage;
import com.juneth.ota_updates_backend.entity.UpdateAssignment;
import com.juneth.ota_updates_backend.enums.UpdateAssignmentStatus;
import org.springframework.stereotype.Component;

@Component
public class UpdateAssignmentMapper {

    public UpdateAssignment toEntity(Device device, DeviceGroup group, FirmwareImage firmwareImage) {
        return UpdateAssignment.builder()
                .device(device)
                .deviceGroup(group)
                .firmwareImage(firmwareImage)
                .status(UpdateAssignmentStatus.PENDING)
                .build();
    }

    public UpdateAssignmentResponse toResponse(UpdateAssignment assignment) {
        if (assignment == null) {
            return null;
        }
        return UpdateAssignmentResponse.builder()
                .id(assignment.getId())
                .deviceId(assignment.getDevice() != null ? assignment.getDevice().getId() : null)
                .deviceName(assignment.getDevice() != null ? assignment.getDevice().getName() : null)
                .groupId(assignment.getDeviceGroup() != null ? assignment.getDeviceGroup().getId() : null)
                .groupName(assignment.getDeviceGroup() != null ? assignment.getDeviceGroup().getName() : null)
                .firmwareImageId(assignment.getFirmwareImage() != null ? assignment.getFirmwareImage().getId() : null)
                .firmwareVersion(assignment.getFirmwareImage() != null ? assignment.getFirmwareImage().getVersion() : null)
                .chipTypeName(assignment.getFirmwareImage() != null && assignment.getFirmwareImage().getChipType() != null
                        ? assignment.getFirmwareImage().getChipType().getName()
                        : null)
                .status(assignment.getStatus())
                .createdAt(assignment.getCreatedAt())
                .updatedAt(assignment.getUpdatedAt())
                .build();
    }
}

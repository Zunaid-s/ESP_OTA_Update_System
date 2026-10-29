package com.juneth.ota_updates_backend.mapper;

import com.juneth.ota_updates_backend.dto.request.RecordUpdateProgressRequest;
import com.juneth.ota_updates_backend.dto.response.UpdateHistoryResponse;
import com.juneth.ota_updates_backend.entity.Device;
import com.juneth.ota_updates_backend.entity.FirmwareImage;
import com.juneth.ota_updates_backend.entity.UpdateAssignment;
import com.juneth.ota_updates_backend.entity.UpdateHistory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UpdateHistoryMapper {

    public UpdateHistory toEntity(
            RecordUpdateProgressRequest request,
            Device device,
            UpdateAssignment assignment,
            FirmwareImage firmwareImage
    ) {
        if (request == null) {
            return null;
        }
        return UpdateHistory.builder()
                .device(device)
                .updateAssignment(assignment)
                .firmwareImage(firmwareImage)
                .previousVersion(request.previousVersion())
                .newVersion(request.newVersion())
                .status(request.status())
                .errorMessage(request.errorMessage())
                .startedAt(LocalDateTime.now())
                .build();
    }

    public UpdateHistoryResponse toResponse(UpdateHistory history) {
        if (history == null) {
            return null;
        }
        return UpdateHistoryResponse.builder()
                .id(history.getId())
                .deviceId(history.getDevice() != null ? history.getDevice().getId() : null)
                .deviceName(history.getDevice() != null ? history.getDevice().getName() : null)
                .assignmentId(history.getUpdateAssignment() != null ? history.getUpdateAssignment().getId() : null)
                .firmwareImageId(history.getFirmwareImage() != null ? history.getFirmwareImage().getId() : null)
                .firmwareVersion(history.getFirmwareImage() != null ? history.getFirmwareImage().getVersion() : null)
                .previousVersion(history.getPreviousVersion())
                .newVersion(history.getNewVersion())
                .status(history.getStatus())
                .errorMessage(history.getErrorMessage())
                .startedAt(history.getStartedAt())
                .completedAt(history.getCompletedAt())
                .createdAt(history.getCreatedAt())
                .build();
    }
}

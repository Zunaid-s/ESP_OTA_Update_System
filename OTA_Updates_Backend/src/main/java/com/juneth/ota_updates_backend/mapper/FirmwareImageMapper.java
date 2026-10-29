package com.juneth.ota_updates_backend.mapper;

import com.juneth.ota_updates_backend.dto.request.UploadFirmwareMetadataRequest;
import com.juneth.ota_updates_backend.dto.response.FirmwareImageResponse;
import com.juneth.ota_updates_backend.entity.ChipType;
import com.juneth.ota_updates_backend.entity.FirmwareImage;
import org.springframework.stereotype.Component;

@Component
public class FirmwareImageMapper {

    public FirmwareImage toEntity(
            UploadFirmwareMetadataRequest request,
            ChipType chipType,
            String fileName,
            String filePath,
            Long fileSize,
            String checksum
    ) {
        if (request == null) {
            return null;
        }
        return FirmwareImage.builder()
                .version(request.version())
                .chipType(chipType)
                .fileName(fileName)
                .filePath(filePath)
                .fileSize(fileSize)
                .checksum(checksum)
                .signature(request.signature())
                .isGeneric(request.isGeneric() != null ? request.isGeneric() : false)
                .description(request.description())
                .build();
    }

    public FirmwareImageResponse toResponse(FirmwareImage image) {
        if (image == null) {
            return null;
        }
        return FirmwareImageResponse.builder()
                .id(image.getId())
                .version(image.getVersion())
                .chipTypeId(image.getChipType() != null ? image.getChipType().getId() : null)
                .chipTypeName(image.getChipType() != null ? image.getChipType().getName() : null)
                .fileName(image.getFileName())
                .fileSize(image.getFileSize())
                .checksum(image.getChecksum())
                .signature(image.getSignature())
                .isGeneric(image.getIsGeneric())
                .description(image.getDescription())
                .createdAt(image.getCreatedAt())
                .updatedAt(image.getUpdatedAt())
                .build();
    }
}

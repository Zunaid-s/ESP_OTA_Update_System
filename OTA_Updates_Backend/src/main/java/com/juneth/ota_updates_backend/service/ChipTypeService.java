package com.juneth.ota_updates_backend.service;

import com.juneth.ota_updates_backend.dto.request.CreateChipTypeRequest;
import com.juneth.ota_updates_backend.dto.request.UpdateChipTypeRequest;
import com.juneth.ota_updates_backend.dto.response.ChipTypeResponse;
import com.juneth.ota_updates_backend.entity.ChipType;
import com.juneth.ota_updates_backend.entity.DeviceGroup;
import com.juneth.ota_updates_backend.exception.DuplicateResourceException;
import com.juneth.ota_updates_backend.exception.InvalidOperationException;
import com.juneth.ota_updates_backend.exception.ResourceNotFoundException;
import com.juneth.ota_updates_backend.mapper.ChipTypeMapper;
import com.juneth.ota_updates_backend.repository.ChipTypeRepository;
import com.juneth.ota_updates_backend.repository.DeviceGroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChipTypeService {

    private final ChipTypeRepository chipTypeRepository;
    private final DeviceGroupRepository deviceGroupRepository;
    private final ChipTypeMapper chipTypeMapper;

    @Transactional
    public ChipTypeResponse createChipType(CreateChipTypeRequest request) {
        log.info("Creating new chip type: {}", request.name());
        if (chipTypeRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("ChipType", "name", request.name());
        }

        ChipType chipType = chipTypeMapper.toEntity(request);
        ChipType saved = chipTypeRepository.save(chipType);

        // Requirement: Auto-create corresponding DeviceGroup if it doesn't already exist
        if (!deviceGroupRepository.existsByName(request.name())) {
            DeviceGroup autoGroup = DeviceGroup.builder()
                    .name(request.name())
                    .description("Auto-created group for " + request.name() + " nodes")
                    .build();
            deviceGroupRepository.save(autoGroup);
            log.info("Auto-created matching device group: {}", request.name());
        }

        return chipTypeMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ChipTypeResponse> getAllChipTypes() {
        return chipTypeRepository.findAll().stream()
                .map(chipTypeMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ChipTypeResponse getChipTypeById(UUID id) {
        return chipTypeRepository.findById(id)
                .map(chipTypeMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("ChipType", id));
    }

    @Transactional
    public ChipTypeResponse updateChipType(UUID id, UpdateChipTypeRequest request) {
        log.info("Updating chip type ID: {}", id);
        ChipType chipType = chipTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ChipType", id));

        chipTypeMapper.updateEntity(chipType, request);
        ChipType updated = chipTypeRepository.save(chipType);
        return chipTypeMapper.toResponse(updated);
    }

    @Transactional
    public void deleteChipType(UUID id) {
        log.info("Deleting chip type ID: {}", id);
        ChipType chipType = chipTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ChipType", id));

        if (chipType.getDevices() != null && !chipType.getDevices().isEmpty()) {
            throw new InvalidOperationException("Cannot delete ChipType because active devices are attached to it");
        }

        if (chipType.getFirmwareImages() != null && !chipType.getFirmwareImages().isEmpty()) {
            throw new InvalidOperationException("Cannot delete ChipType because firmware images are associated with it");
        }

        chipTypeRepository.delete(chipType);
        log.info("Deleted chip type ID: {}", id);
    }
}

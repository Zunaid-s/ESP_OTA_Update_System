package com.juneth.ota_updates_backend.service;

import com.juneth.ota_updates_backend.dto.request.CreateDeviceGroupRequest;
import com.juneth.ota_updates_backend.dto.request.UpdateDeviceGroupRequest;
import com.juneth.ota_updates_backend.dto.response.DeviceGroupResponse;
import com.juneth.ota_updates_backend.dto.response.DeviceResponse;
import com.juneth.ota_updates_backend.entity.Device;
import com.juneth.ota_updates_backend.entity.DeviceGroup;
import com.juneth.ota_updates_backend.entity.UpdateAssignment;
import com.juneth.ota_updates_backend.enums.UpdateAssignmentStatus;
import com.juneth.ota_updates_backend.exception.DuplicateResourceException;
import com.juneth.ota_updates_backend.exception.InvalidOperationException;
import com.juneth.ota_updates_backend.exception.ResourceNotFoundException;
import com.juneth.ota_updates_backend.mapper.DeviceGroupMapper;
import com.juneth.ota_updates_backend.mapper.DeviceMapper;
import com.juneth.ota_updates_backend.repository.DeviceGroupRepository;
import com.juneth.ota_updates_backend.repository.DeviceRepository;
import com.juneth.ota_updates_backend.repository.UpdateAssignmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceGroupService {

    private final DeviceGroupRepository deviceGroupRepository;
    private final DeviceRepository deviceRepository;
    private final UpdateAssignmentRepository updateAssignmentRepository;
    private final DeviceGroupMapper deviceGroupMapper;
    private final DeviceMapper deviceMapper;

    @Transactional
    public DeviceGroupResponse createGroup(CreateDeviceGroupRequest request) {
        log.info("Creating device group: {}", request.name());
        if (deviceGroupRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("DeviceGroup", "name", request.name());
        }

        DeviceGroup group = deviceGroupMapper.toEntity(request);
        DeviceGroup saved = deviceGroupRepository.save(group);
        log.info("Created device group '{}' with ID: {}", saved.getName(), saved.getId());
        return deviceGroupMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<DeviceGroupResponse> getAllGroups() {
        return deviceGroupRepository.findAll().stream()
                .map(deviceGroupMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public DeviceGroupResponse getGroupById(UUID id) {
        return deviceGroupRepository.findById(id)
                .map(deviceGroupMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("DeviceGroup", id));
    }

    @Transactional(readOnly = true)
    public List<DeviceResponse> getDevicesInGroup(UUID groupId) {
        if (!deviceGroupRepository.existsById(groupId)) {
            throw new ResourceNotFoundException("DeviceGroup", groupId);
        }
        return deviceRepository.findByDeviceGroupsId(groupId).stream()
                .map(deviceMapper::toResponse)
                .toList();
    }

    @Transactional
    public DeviceGroupResponse updateGroup(UUID id, UpdateDeviceGroupRequest request) {
        log.info("Updating device group ID: {}", id);
        DeviceGroup group = deviceGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DeviceGroup", id));

        if (request.name() != null && !request.name().isBlank() && !request.name().equals(group.getName())) {
            if (deviceGroupRepository.existsByName(request.name())) {
                throw new DuplicateResourceException("DeviceGroup", "name", request.name());
            }
        }

        deviceGroupMapper.updateEntity(group, request);
        DeviceGroup updated = deviceGroupRepository.save(group);
        log.info("Updated device group ID: {}", id);
        return deviceGroupMapper.toResponse(updated);
    }

    @Transactional
    public void deleteGroup(UUID id) {
        log.info("Deleting device group ID: {}", id);
        DeviceGroup group = deviceGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DeviceGroup", id));

        List<UpdateAssignment> pending = updateAssignmentRepository.findByDeviceGroupIdAndStatus(id, UpdateAssignmentStatus.PENDING);
        List<UpdateAssignment> inProgress = updateAssignmentRepository.findByDeviceGroupIdAndStatus(id, UpdateAssignmentStatus.IN_PROGRESS);

        if (!pending.isEmpty() || !inProgress.isEmpty()) {
            throw new InvalidOperationException("Cannot delete DeviceGroup because it has active update assignments");
        }

        // Dissociate group from all devices before deletion
        List<Device> devices = deviceRepository.findByDeviceGroupsId(id);
        for (Device device : devices) {
            device.getDeviceGroups().remove(group);
            deviceRepository.save(device);
        }

        deviceGroupRepository.delete(group);
        log.info("Deleted device group ID: {}", id);
    }
}

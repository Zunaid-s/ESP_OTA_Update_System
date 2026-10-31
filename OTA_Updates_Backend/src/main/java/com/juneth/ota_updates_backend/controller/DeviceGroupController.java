package com.juneth.ota_updates_backend.controller;

import com.juneth.ota_updates_backend.dto.request.CreateDeviceGroupRequest;
import com.juneth.ota_updates_backend.dto.request.UpdateDeviceGroupRequest;
import com.juneth.ota_updates_backend.dto.response.DeviceGroupResponse;
import com.juneth.ota_updates_backend.dto.response.DeviceResponse;
import com.juneth.ota_updates_backend.service.DeviceGroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/device-groups")
@RequiredArgsConstructor
public class DeviceGroupController {

    private final DeviceGroupService deviceGroupService;

    @PostMapping
    public ResponseEntity<DeviceGroupResponse> createGroup(@Valid @RequestBody CreateDeviceGroupRequest request) {
        DeviceGroupResponse response = deviceGroupService.createGroup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<DeviceGroupResponse>> getAllGroups() {
        return ResponseEntity.ok(deviceGroupService.getAllGroups());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceGroupResponse> getGroupById(@PathVariable UUID id) {
        return ResponseEntity.ok(deviceGroupService.getGroupById(id));
    }

    @GetMapping("/{id}/devices")
    public ResponseEntity<List<DeviceResponse>> getDevicesInGroup(@PathVariable UUID id) {
        return ResponseEntity.ok(deviceGroupService.getDevicesInGroup(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeviceGroupResponse> updateGroup(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateDeviceGroupRequest request) {
        return ResponseEntity.ok(deviceGroupService.updateGroup(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable UUID id) {
        deviceGroupService.deleteGroup(id);
        return ResponseEntity.noContent().build();
    }
}

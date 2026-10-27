package com.juneth.ota_updates_backend.repository;

import com.juneth.ota_updates_backend.entity.Device;
import com.juneth.ota_updates_backend.enums.DeviceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeviceRepository extends JpaRepository<Device, UUID> {
    Optional<Device> findByMacAddress(String macAddress);

    Optional<Device> findByApiKey(String apiKey);

    List<Device> findByChipTypeId(UUID chipTypeId);

    List<Device> findByStatus(DeviceStatus status);

    List<Device> findByDeviceGroupsId(UUID groupId);

    boolean existsByMacAddress(String macAddress);

    boolean existsByApiKey(String apiKey);
}

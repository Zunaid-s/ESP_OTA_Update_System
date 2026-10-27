package com.juneth.ota_updates_backend.repository;

import com.juneth.ota_updates_backend.entity.DeviceGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeviceGroupRepository extends JpaRepository<DeviceGroup, UUID> {
    Optional<DeviceGroup> findByName(String name);

    boolean existsByName(String name);
}

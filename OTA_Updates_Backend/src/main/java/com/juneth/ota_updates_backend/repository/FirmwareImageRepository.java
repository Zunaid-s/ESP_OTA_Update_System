package com.juneth.ota_updates_backend.repository;

import com.juneth.ota_updates_backend.entity.FirmwareImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FirmwareImageRepository extends JpaRepository<FirmwareImage, UUID> {
    List<FirmwareImage> findByChipTypeId(UUID chipTypeId);

    Optional<FirmwareImage> findByChipTypeIdAndVersion(UUID chipTypeId, String version);

    List<FirmwareImage> findByVersion(String version);

    boolean existsByChipTypeIdAndVersion(UUID chipTypeId, String version);
}

package com.juneth.ota_updates_backend.repository;

import com.juneth.ota_updates_backend.entity.ChipType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChipTypeRepository extends JpaRepository<ChipType, UUID> {
    Optional<ChipType> findByName(String name);

    boolean existsByName(String name);
}

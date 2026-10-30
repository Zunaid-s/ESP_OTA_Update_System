package com.juneth.ota_updates_backend.seeder;

import com.juneth.ota_updates_backend.entity.ChipType;
import com.juneth.ota_updates_backend.entity.DeviceGroup;
import com.juneth.ota_updates_backend.repository.ChipTypeRepository;
import com.juneth.ota_updates_backend.repository.DeviceGroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "app.seeder.enabled", havingValue = "true", matchIfMissing = true)
public class DataSeeder implements CommandLineRunner {

    public record BoardSpec(String name, String description, Integer flashSize) {}

    public static final List<BoardSpec> INITIAL_BOARDS = List.of(
            new BoardSpec("NodeMCU-32S", "NodeMCU ESP32-S 38-pin development board", 4 * 1024 * 1024),
            new BoardSpec("ESP32-s3", "ESP32-S3 dual-core Xtensa LX7 development board", 8 * 1024 * 1024),
            new BoardSpec("ESP32-S3-SuperMini", "ESP32-S3 SuperMini ultra-compact development board", 4 * 1024 * 1024),
            new BoardSpec("ESP32-C3", "ESP32-C3 RISC-V single-core development board", 4 * 1024 * 1024),
            new BoardSpec("ESP32-C3-SuperMini", "ESP32-C3 SuperMini compact development board", 4 * 1024 * 1024)
    );

    private final ChipTypeRepository chipTypeRepository;
    private final DeviceGroupRepository deviceGroupRepository;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Starting DataSeeder for initial chip types and device groups...");
        seedInitialBoards();
        log.info("DataSeeder completed successfully.");
    }

    public void seedInitialBoards() {
        for (BoardSpec board : INITIAL_BOARDS) {
            seedChipType(board);
            seedDeviceGroup(board);
        }
    }

    private void seedChipType(BoardSpec board) {
        if (!chipTypeRepository.existsByName(board.name())) {
            ChipType chipType = ChipType.builder()
                    .name(board.name())
                    .description(board.description())
                    .flashSize(board.flashSize())
                    .build();
            chipTypeRepository.save(chipType);
            log.info("Seeded ChipType: {} (Flash: {} bytes)", board.name(), board.flashSize());
        } else {
            log.debug("ChipType '{}' already exists, skipping.", board.name());
        }
    }

    private void seedDeviceGroup(BoardSpec board) {
        if (!deviceGroupRepository.existsByName(board.name())) {
            DeviceGroup deviceGroup = DeviceGroup.builder()
                    .name(board.name())
                    .description("Auto-created group for " + board.name() + " nodes")
                    .build();
            deviceGroupRepository.save(deviceGroup);
            log.info("Seeded auto-created DeviceGroup: {}", board.name());
        } else {
            log.debug("DeviceGroup '{}' already exists, skipping.", board.name());
        }
    }
}

package com.juneth.ota_updates_backend.seeder;

import com.juneth.ota_updates_backend.entity.ChipType;
import com.juneth.ota_updates_backend.entity.DeviceGroup;
import com.juneth.ota_updates_backend.repository.ChipTypeRepository;
import com.juneth.ota_updates_backend.repository.DeviceGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DataSeederTest {

    @Autowired
    private ChipTypeRepository chipTypeRepository;

    @Autowired
    private DeviceGroupRepository deviceGroupRepository;

    @Autowired
    private DataSeeder dataSeeder;

    @Test
    @DisplayName("Should seed all initial chip types and auto-created device groups on startup")
    void testInitialBoardsAndGroupsAreSeeded() {
        for (DataSeeder.BoardSpec spec : DataSeeder.INITIAL_BOARDS) {
            // Verify ChipType exists and has correct flash size
            Optional<ChipType> chipTypeOpt = chipTypeRepository.findByName(spec.name());
            assertThat(chipTypeOpt)
                    .as("ChipType '%s' should be present", spec.name())
                    .isPresent();

            ChipType chipType = chipTypeOpt.get();
            assertThat(chipType.getFlashSize())
                    .as("ChipType '%s' flash size should be %d bytes", spec.name(), spec.flashSize())
                    .isEqualTo(spec.flashSize());

            assertThat(chipType.getDescription())
                    .as("ChipType '%s' should have a description", spec.name())
                    .isNotBlank();

            // Verify corresponding DeviceGroup exists
            Optional<DeviceGroup> groupOpt = deviceGroupRepository.findByName(spec.name());
            assertThat(groupOpt)
                    .as("DeviceGroup '%s' should be present", spec.name())
                    .isPresent();

            DeviceGroup group = groupOpt.get();
            assertThat(group.getDescription())
                    .as("DeviceGroup '%s' description should mention auto-created", spec.name())
                    .contains("Auto-created");
        }
    }

    @Test
    @DisplayName("DataSeeder should be idempotent when executed multiple times")
    void testDataSeederIsIdempotent() {
        long chipTypeCountBefore = chipTypeRepository.count();
        long groupCountBefore = deviceGroupRepository.count();

        // Re-run the seeder
        dataSeeder.run();

        long chipTypeCountAfter = chipTypeRepository.count();
        long groupCountAfter = deviceGroupRepository.count();

        assertThat(chipTypeCountAfter)
                .as("Re-running DataSeeder should not create duplicate ChipTypes")
                .isEqualTo(chipTypeCountBefore);

        assertThat(groupCountAfter)
                .as("Re-running DataSeeder should not create duplicate DeviceGroups")
                .isEqualTo(groupCountBefore);
    }
}

package com.juneth.ota_updates_backend.controller;

import com.juneth.ota_updates_backend.dto.request.CreateChipTypeRequest;
import com.juneth.ota_updates_backend.dto.request.UpdateChipTypeRequest;
import com.juneth.ota_updates_backend.entity.ChipType;
import com.juneth.ota_updates_backend.entity.DeviceGroup;
import com.juneth.ota_updates_backend.repository.ChipTypeRepository;
import com.juneth.ota_updates_backend.repository.DeviceGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ChipTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ChipTypeRepository chipTypeRepository;

    @Autowired
    private DeviceGroupRepository deviceGroupRepository;

    @Test
    @DisplayName("GET /api/chip-types should return list containing seeded boards")
    void shouldGetAllChipTypes() throws Exception {
        mockMvc.perform(get("/api/chip-types")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(5))))
                .andExpect(jsonPath("$[*].name", hasItems(
                        "NodeMCU-32S",
                        "ESP32-s3",
                        "ESP32-S3-SuperMini",
                        "ESP32-C3",
                        "ESP32-C3-SuperMini"
                )));
    }

    @Test
    @DisplayName("GET /api/chip-types/{id} should return chip type when found")
    void shouldGetChipTypeById() throws Exception {
        ChipType seeded = chipTypeRepository.findByName("NodeMCU-32S").orElseThrow();

        mockMvc.perform(get("/api/chip-types/{id}", seeded.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(seeded.getId().toString())))
                .andExpect(jsonPath("$.name", is("NodeMCU-32S")))
                .andExpect(jsonPath("$.flashSize", is(4 * 1024 * 1024)));
    }

    @Test
    @DisplayName("GET /api/chip-types/{id} should return 404 when not found")
    void shouldReturn404WhenNotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(get("/api/chip-types/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", containsString(nonExistentId.toString())));
    }

    @Test
    @DisplayName("POST /api/chip-types should create ChipType and auto-create corresponding DeviceGroup")
    void shouldCreateChipTypeAndAutoCreateDeviceGroup() throws Exception {
        String uniqueBoardName = "ESP8266-Test-" + UUID.randomUUID().toString().substring(0, 8);
        CreateChipTypeRequest request = CreateChipTypeRequest.builder()
                .name(uniqueBoardName)
                .description("ESP8266 WiFi microchip")
                .flashSize(4 * 1024 * 1024)
                .build();

        mockMvc.perform(post("/api/chip-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name", is(uniqueBoardName)))
                .andExpect(jsonPath("$.flashSize", is(4 * 1024 * 1024)));

        // Verify ChipType exists in DB
        Optional<ChipType> createdChip = chipTypeRepository.findByName(uniqueBoardName);
        assertThat(createdChip).isPresent();

        // Verify matching DeviceGroup was automatically created
        Optional<DeviceGroup> autoGroup = deviceGroupRepository.findByName(uniqueBoardName);
        assertThat(autoGroup).isPresent();
        assertThat(autoGroup.get().getDescription()).contains("Auto-created group");
    }

    @Test
    @DisplayName("POST /api/chip-types should return 409 Conflict when name already exists")
    void shouldReturn409WhenDuplicateName() throws Exception {
        CreateChipTypeRequest duplicateRequest = CreateChipTypeRequest.builder()
                .name("NodeMCU-32S") // Already seeded
                .description("Duplicate attempt")
                .flashSize(4 * 1024 * 1024)
                .build();

        mockMvc.perform(post("/api/chip-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("Conflict")))
                .andExpect(jsonPath("$.message", containsString("already exists")));
    }

    @Test
    @DisplayName("POST /api/chip-types should return 400 Bad Request when validation fails")
    void shouldReturn400WhenPayloadInvalid() throws Exception {
        CreateChipTypeRequest invalidRequest = CreateChipTypeRequest.builder()
                .name("") // Blank name
                .flashSize(-100) // Negative flash size
                .build();

        mockMvc.perform(post("/api/chip-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.validationErrors.name").isNotEmpty())
                .andExpect(jsonPath("$.validationErrors.flashSize").isNotEmpty());
    }

    @Test
    @DisplayName("PUT /api/chip-types/{id} should update description and flash size")
    void shouldUpdateChipType() throws Exception {
        String testBoardName = "ESP32-Update-" + UUID.randomUUID().toString().substring(0, 8);
        ChipType created = chipTypeRepository.save(ChipType.builder()
                .name(testBoardName)
                .description("Original description")
                .flashSize(4 * 1024 * 1024)
                .build());

        UpdateChipTypeRequest updateRequest = UpdateChipTypeRequest.builder()
                .description("Updated description")
                .flashSize(8 * 1024 * 1024)
                .build();

        mockMvc.perform(put("/api/chip-types/{id}", created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(created.getId().toString())))
                .andExpect(jsonPath("$.description", is("Updated description")))
                .andExpect(jsonPath("$.flashSize", is(8 * 1024 * 1024)));

        ChipType reloaded = chipTypeRepository.findById(created.getId()).orElseThrow();
        assertThat(reloaded.getDescription()).isEqualTo("Updated description");
        assertThat(reloaded.getFlashSize()).isEqualTo(8 * 1024 * 1024);
    }

    @Test
    @DisplayName("DELETE /api/chip-types/{id} should delete unattached chip type")
    void shouldDeleteChipType() throws Exception {
        String testBoardName = "ESP32-Delete-" + UUID.randomUUID().toString().substring(0, 8);
        ChipType created = chipTypeRepository.save(ChipType.builder()
                .name(testBoardName)
                .description("To be deleted")
                .flashSize(4 * 1024 * 1024)
                .build());

        mockMvc.perform(delete("/api/chip-types/{id}", created.getId()))
                .andExpect(status().isNoContent());

        assertThat(chipTypeRepository.findById(created.getId())).isEmpty();
    }
}

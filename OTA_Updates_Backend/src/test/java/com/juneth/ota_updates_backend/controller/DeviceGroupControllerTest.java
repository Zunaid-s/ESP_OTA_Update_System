package com.juneth.ota_updates_backend.controller;

import com.juneth.ota_updates_backend.dto.request.CreateDeviceGroupRequest;
import com.juneth.ota_updates_backend.dto.request.UpdateDeviceGroupRequest;
import com.juneth.ota_updates_backend.entity.DeviceGroup;
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
class DeviceGroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DeviceGroupRepository deviceGroupRepository;

    @Test
    @DisplayName("GET /api/device-groups should return list containing seeded groups")
    void shouldGetAllGroups() throws Exception {
        mockMvc.perform(get("/api/device-groups")
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
    @DisplayName("GET /api/device-groups/{id} should return group when found")
    void shouldGetGroupById() throws Exception {
        DeviceGroup seeded = deviceGroupRepository.findByName("NodeMCU-32S").orElseThrow();

        mockMvc.perform(get("/api/device-groups/{id}", seeded.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(seeded.getId().toString())))
                .andExpect(jsonPath("$.name", is("NodeMCU-32S")))
                .andExpect(jsonPath("$.description", containsString("Auto-created group")));
    }

    @Test
    @DisplayName("GET /api/device-groups/{id} should return 404 when not found")
    void shouldReturn404WhenNotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(get("/api/device-groups/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", containsString(nonExistentId.toString())));
    }

    @Test
    @DisplayName("POST /api/device-groups should create a new device group")
    void shouldCreateNewDeviceGroup() throws Exception {
        String uniqueGroupName = "Living-Room-Fleet-" + UUID.randomUUID().toString().substring(0, 8);
        CreateDeviceGroupRequest request = CreateDeviceGroupRequest.builder()
                .name(uniqueGroupName)
                .description("Devices deployed in the living room")
                .build();

        mockMvc.perform(post("/api/device-groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name", is(uniqueGroupName)))
                .andExpect(jsonPath("$.description", is("Devices deployed in the living room")));

        Optional<DeviceGroup> created = deviceGroupRepository.findByName(uniqueGroupName);
        assertThat(created).isPresent();
    }

    @Test
    @DisplayName("POST /api/device-groups should return 409 Conflict when name already exists")
    void shouldReturn409WhenDuplicateName() throws Exception {
        CreateDeviceGroupRequest duplicateRequest = CreateDeviceGroupRequest.builder()
                .name("NodeMCU-32S") // Already seeded
                .description("Duplicate group attempt")
                .build();

        mockMvc.perform(post("/api/device-groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("Conflict")))
                .andExpect(jsonPath("$.message", containsString("already exists")));
    }

    @Test
    @DisplayName("POST /api/device-groups should return 400 Bad Request when name is blank")
    void shouldReturn400WhenNameIsBlank() throws Exception {
        CreateDeviceGroupRequest invalidRequest = CreateDeviceGroupRequest.builder()
                .name("")
                .description("No name provided")
                .build();

        mockMvc.perform(post("/api/device-groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.validationErrors.name").isNotEmpty());
    }

    @Test
    @DisplayName("GET /api/device-groups/{id}/devices should return devices belonging to the group")
    void shouldGetDevicesInGroup() throws Exception {
        DeviceGroup group = deviceGroupRepository.findByName("NodeMCU-32S").orElseThrow();

        mockMvc.perform(get("/api/device-groups/{id}/devices", group.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", isA(java.util.List.class)));
    }

    @Test
    @DisplayName("PUT /api/device-groups/{id} should update group details")
    void shouldUpdateGroup() throws Exception {
        String originalName = "Temp-Group-" + UUID.randomUUID().toString().substring(0, 8);
        DeviceGroup created = deviceGroupRepository.save(DeviceGroup.builder()
                .name(originalName)
                .description("Original description")
                .build());

        String updatedName = "Updated-Group-" + UUID.randomUUID().toString().substring(0, 8);
        UpdateDeviceGroupRequest updateRequest = UpdateDeviceGroupRequest.builder()
                .name(updatedName)
                .description("Updated description")
                .build();

        mockMvc.perform(put("/api/device-groups/{id}", created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(created.getId().toString())))
                .andExpect(jsonPath("$.name", is(updatedName)))
                .andExpect(jsonPath("$.description", is("Updated description")));

        DeviceGroup reloaded = deviceGroupRepository.findById(created.getId()).orElseThrow();
        assertThat(reloaded.getName()).isEqualTo(updatedName);
        assertThat(reloaded.getDescription()).isEqualTo("Updated description");
    }

    @Test
    @DisplayName("DELETE /api/device-groups/{id} should delete group")
    void shouldDeleteGroup() throws Exception {
        String testGroupName = "To-Delete-" + UUID.randomUUID().toString().substring(0, 8);
        DeviceGroup created = deviceGroupRepository.save(DeviceGroup.builder()
                .name(testGroupName)
                .description("Will be deleted")
                .build());

        mockMvc.perform(delete("/api/device-groups/{id}", created.getId()))
                .andExpect(status().isNoContent());

        assertThat(deviceGroupRepository.findById(created.getId())).isEmpty();
    }
}

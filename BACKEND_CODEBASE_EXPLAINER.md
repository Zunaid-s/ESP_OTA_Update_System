# OTA Updates Backend — Codebase Architecture & Class Explainer

This document provides a comprehensive guide to every package and Java class in the `OTA_Updates_Backend` service (`com.juneth.ota_updates_backend`).

---

## Table of Contents
1. [Root Application](#1-root-application)
2. [Domain Entities (`entity`)](#2-domain-entities-entity)
3. [Domain Enums (`enums`)](#3-domain-enums-enums)
4. [Data Access Layer (`repository`)](#4-data-access-layer-repository)
5. [Request DTOs (`dto.request`)](#5-request-dtos-dtorequest)
6. [Response DTOs (`dto.response`)](#6-response-dtos-dtoresponse)
7. [Mapping Components (`mapper`)](#7-mapping-components-mapper)

---

## 1. Root Application

**Package:** `com.juneth.ota_updates_backend`

### `OtaUpdatesBackendApplication.java`
- **Purpose:** Entry point for the Spring Boot application.
- **Key Responsibilities:**
  - Bootstraps the Spring ApplicationContext via `@SpringBootApplication`.
  - Enables auto-configuration for Spring MVC, Spring Data JPA, Flyway database migrations, WebSocket, and MQTT integration.
  - Hosts the standard `main(String[] args)` method.

---

## 2. Domain Entities (`entity`)

**Package:** `com.juneth.ota_updates_backend.entity`  
These classes represent PostgreSQL relational database tables mapped through Jakarta Persistence API (JPA) with Hibernate.

```
┌──────────────┐          ┌───────────────────────────┐          ┌───────────────┐
│   ChipType   │ 1 ──── * │          Device           │ * ──── * │  DeviceGroup  │
└──────────────┘          └───────────────────────────┘          └───────────────┘
       │ 1                              │ 1                              │ 1
       │                                │                                │
       ▼ *                              ▼ *                              ▼ *
┌──────────────┐          ┌───────────────────────────┐                  │
│FirmwareImage │ 1 ──── * │     UpdateAssignment      │ * ───────────────┘
└──────────────┘          └───────────────────────────┘
       │ 1                              │ 1
       │                                │
       ▼ *                              ▼ *
┌─────────────────────────────────────────────────────┐
│                    UpdateHistory                    │
└─────────────────────────────────────────────────────┘
```

### `ChipType.java` (`chip_types` table)
- **Purpose:** Represents a supported hardware architecture / microcontroller board variant (e.g., `NodeMCU-32S`, `ESP32-s3`, `ESP32-S3-SuperMini`, `ESP32-C3`, `ESP32-C3-SuperMini`).
- **Key Fields:**
  - `id` (UUID): Primary key.
  - `name` (String, unique): Name of the microcontroller board.
  - `description` (String): Human-readable details.
  - `flashSize` (Integer): Total flash memory size in bytes (e.g., `4194304` for 4MB). Used by the backend to reject oversized firmware binaries before delivery.
  - `createdAt` (LocalDateTime): Creation audit timestamp.
  - `devices` (List<Device>): Devices using this chip type.
  - `firmwareImages` (List<FirmwareImage>): Firmware images compiled for this chip type.

### `Device.java` (`devices` table)
- **Purpose:** Represents an individual registered/paired physical ESP32 node.
- **Key Fields:**
  - `id` (UUID): Unique server-generated device identifier.
  - `name` (String): User-assigned friendly label (e.g., "Living Room Sensor").
  - `chipType` (ChipType): Microcontroller variant (Foreign Key to `chip_types`).
  - `macAddress` (String, unique, 17 chars): Hardware MAC address (`XX:XX:XX:XX:XX:XX`) captured during pairing.
  - `apiKey` (String, unique): Secret token used by the node to authenticate HTTP downloads and status reports.
  - `currentFirmwareVersion` (String): Version of the firmware currently running on the board.
  - `status` (DeviceStatus): Operational state (`UNPAIRED`, `PAIRING`, `PAIRED`, `ACTIVE`, `INACTIVE`).
  - `lastSeenAt` (LocalDateTime): Last heartbeat timestamp received via MQTT or HTTP.
  - `deviceGroups` (Set<DeviceGroup>): Groups this device belongs to (Many-to-Many via `device_group_memberships`).
  - `updateAssignments` (List<UpdateAssignment>): Direct update assignments targeted at this device.
  - `updateHistories` (List<UpdateHistory>): Complete history log of all update attempts on this device.

### `DeviceGroup.java` (`device_groups` table)
- **Purpose:** Represents a logical collection of devices that can receive updates simultaneously. Groups are either user-created (e.g., "Kitchen Fleet") or automatically created per board type (e.g., `NodeMCU-32S`, `ESP32-s3`, etc.).
- **Key Fields:**
  - `id` (UUID): Primary key.
  - `name` (String, unique): Name of the group.
  - `description` (String): Purpose or location description.
  - `devices` (Set<Device>): Set of member devices (Many-to-Many via `device_group_memberships`).
  - `updateAssignments` (List<UpdateAssignment>): Batch update assignments targeted at this entire group.

### `FirmwareImage.java` (`firmware_images` table)
- **Purpose:** Represents a compiled `.bin` firmware file uploaded to the backend and ready for distribution.
- **Key Fields:**
  - `id` (UUID): Primary key.
  - `version` (String): Semantic version or build string (e.g., `"1.0.4"`).
  - `chipType` (ChipType): Target board architecture (must match device's chip type).
  - `fileName` (String): Original uploaded file name (e.g., `"firmware_v1.0.4.bin"`).
  - `filePath` (String): Server filesystem path where the binary is securely stored.
  - `fileSize` (Long): Exact size in bytes.
  - `checksum` (String): Cryptographic hash (e.g., SHA-256) for transmission integrity verification.
  - `signature` (String, NOT NULL): Digital cryptographic signature. **Mandatory:** ESP32 nodes verify this signature and reject unsigned binaries.
  - `isGeneric` (Boolean): Indicates whether the binary is general-purpose or device-specific.
  - `description` (String): Release notes or build details.

### `UpdateAssignment.java` (`update_assignments` table)
- **Purpose:** Represents an intent/command to update either an individual device or an entire device group with a specific firmware image.
- **Key Fields:**
  - `id` (UUID): Primary key.
  - `device` (Device, nullable): Target device (if targeted individually).
  - `deviceGroup` (DeviceGroup, nullable): Target group (if targeted as a group).
  - `firmwareImage` (FirmwareImage): Firmware binary to deploy.
  - `status` (UpdateAssignmentStatus): Current status (`PENDING`, `IN_PROGRESS`, `COMPLETED`, `FAILED`, `ROLLED_BACK`).

### `UpdateHistory.java` (`update_history` table)
- **Purpose:** Immutable audit record of each update attempt executed on a physical device.
- **Key Fields:**
  - `id` (UUID): Primary key.
  - `device` (Device): Physical device that underwent the update.
  - `updateAssignment` (UpdateAssignment, nullable): Associated update assignment.
  - `firmwareImage` (FirmwareImage): Binary that was flashed.
  - `previousVersion` (String): Version before the update attempt.
  - `newVersion` (String): Version intended to be installed.
  - `status` (UpdateHistoryStatus): Execution phase (`STARTED`, `DOWNLOADING`, `DOWNLOADED`, `APPLYING`, `SUCCESS`, `FAILED`, `ROLLED_BACK`).
  - `errorMessage` (String): Diagnostic message if flashing or verification failed.
  - `startedAt` (LocalDateTime): When the update cycle started.
  - `completedAt` (LocalDateTime): When the cycle concluded.

---

## 3. Domain Enums (`enums`)

**Package:** `com.juneth.ota_updates_backend.enums`

### `DeviceStatus.java`
- **Purpose:** Tracks the lifecycle and operational health of an ESP32 node.
  - `UNPAIRED`: Node has not been paired with the backend.
  - `PAIRING`: In the process of initial onboarding and token exchange.
  - `PAIRED`: Successfully paired and registered, idle.
  - `ACTIVE`: Actively sending heartbeats and communicating with the server.
  - `INACTIVE`: Offline / heartbeat expired.

### `UpdateAssignmentStatus.java`
- **Purpose:** High-level status of an update assignment batch or target.
  - `PENDING`: Queued, awaiting pickup or push notification.
  - `IN_PROGRESS`: Device(s) currently downloading or applying the update.
  - `COMPLETED`: Successfully flashed on all target nodes.
  - `FAILED`: Update process failed.
  - `ROLLED_BACK`: Update failed and node safely rolled back to previous firmware partition.

### `UpdateHistoryStatus.java`
- **Purpose:** Granular step-by-step telemetry reported by the ESP32 node during OTA execution.
  - `STARTED`: Node received notification and initiated update procedure.
  - `DOWNLOADING`: HTTP streaming of binary file in progress.
  - `DOWNLOADED`: Binary fully received and checksum/signature validated in RAM.
  - `APPLYING`: Writing binary to inactive OTA partition (`ota_0` or `ota_1`).
  - `SUCCESS`: Node booted from new partition and verified app rollback validity.
  - `FAILED`: Network drop, write error, or signature mismatch during update.
  - `ROLLED_BACK`: Node failed to boot or validation failed, reverted to original partition via ESP-IDF OTA rollback.

---

## 4. Data Access Layer (`repository`)

**Package:** `com.juneth.ota_updates_backend.repository`  
Spring Data JPA interfaces providing automated CRUD operations, derived query methods, and JPQL queries.

### `ChipTypeRepository.java`
- Extends `JpaRepository<ChipType, UUID>`.
- `findByName(String name)`: Fetches chip type by board name.
- `existsByName(String name)`: Checks if a board name is already registered.

### `DeviceGroupRepository.java`
- Extends `JpaRepository<DeviceGroup, UUID>`.
- `findByName(String name)`: Look up group by name.
- `existsByName(String name)`: Prevents duplicate group names.

### `DeviceRepository.java`
- Extends `JpaRepository<Device, UUID>`.
- `findByMacAddress(String macAddress)`: Look up node during pairing or HTTP handshake.
- `findByApiKey(String apiKey)`: Authenticates node requests.
- `findByChipTypeId(UUID chipTypeId)`: Finds all devices matching a board architecture.
- `findByStatus(DeviceStatus status)`: Lists devices by status (e.g., all `ACTIVE` nodes).
- `findByDeviceGroupsId(UUID groupId)`: Queries all member devices inside a group.
- `existsByMacAddress(String macAddress)`: Validates MAC uniqueness.
- `existsByApiKey(String apiKey)`: Validates API key uniqueness.

### `FirmwareImageRepository.java`
- Extends `JpaRepository<FirmwareImage, UUID>`.
- `findByChipTypeId(UUID chipTypeId)`: Lists all firmware images built for a chip type.
- `findByChipTypeIdAndVersion(UUID chipTypeId, String version)`: Retrieves specific version for a board.
- `findByVersion(String version)`: Finds images by version string.
- `existsByChipTypeIdAndVersion(UUID chipTypeId, String version)`: Prevents uploading duplicate versions for the same chip type.

### `UpdateAssignmentRepository.java`
- Extends `JpaRepository<UpdateAssignment, UUID>`.
- `findByDeviceId(UUID deviceId)` & `findByDeviceGroupId(UUID groupId)`: Find assignments by target.
- `findByDeviceIdAndStatus(...)` & `findByDeviceGroupIdAndStatus(...)`: Filter assignments by status.
- `findFirstByDeviceIdAndStatusOrderByCreatedAtDesc(...)`: Most recent assignment for a device.
- `findFirstByDeviceGroupIdAndStatusOrderByCreatedAtDesc(...)`: Most recent assignment for a group.
- `findActiveAssignmentsForDevice(UUID deviceId, UpdateAssignmentStatus status)`: Custom JPQL query that queries assignments targeted either **directly at the device** OR **at any group the device belongs to**.

### `UpdateHistoryRepository.java`
- Extends `JpaRepository<UpdateHistory, UUID>`.
- `findByDeviceIdOrderByCreatedAtDesc(UUID deviceId)`: Full history log for a device.
- `findByUpdateAssignmentIdOrderByCreatedAtDesc(UUID assignmentId)`: History entries generated by an assignment.
- `findByDeviceIdAndStatus(...)`: Filter device history by outcome.
- `findFirstByDeviceIdOrderByCreatedAtDesc(UUID deviceId)`: Inspects the latest update attempt.

---

## 5. Request DTOs (`dto.request`)

**Package:** `com.juneth.ota_updates_backend.dto.request`  
Immutable Java records encapsulating incoming JSON payloads with Jakarta Bean Validation.

| Class | Purpose | Key Validations |
|---|---|---|
| `CreateChipTypeRequest` | Register a new board variant | `@NotBlank` name (max 50), `@NotNull` `@Positive` flashSize |
| `UpdateChipTypeRequest` | Update description or flash size | `@Positive` flashSize |
| `CreateDeviceGroupRequest` | Create a new device group | `@NotBlank` name (max 255) |
| `UpdateDeviceGroupRequest` | Rename or describe a group | Max 255 chars |
| `CreateDeviceRequest` | Manually register a device | `@NotBlank` name, `@NotNull` chipTypeId, MAC regex pattern |
| `UpdateDeviceRequest` | Rename device or change status | Name size, status enum |
| `AssignDeviceGroupsRequest` | Assign/replace groups for a device | `@NotNull` Set of group UUIDs |
| `UploadFirmwareMetadataRequest`| Upload binary metadata | `@NotBlank` version, `@NotNull` chipTypeId, **`@NotBlank` signature** |
| `CreateUpdateAssignmentRequest`| Schedule an update | `@NotNull` firmwareId; `@AssertTrue` ensuring either deviceId or groupId is specified (not both) |
| `RecordUpdateProgressRequest` | ESP32 update progress report | `@NotNull` deviceId, firmwareImageId, status |
| `NodeStatusReport` | Heartbeat & telemetry from node | MAC address, firmware version, status, freeHeap, RSSI |

---

## 6. Response DTOs (`dto.response`)

**Package:** `com.juneth.ota_updates_backend.dto.response`  
Immutable Java records carrying formatted data to API consumers, CLI, or WebSocket clients.

| Class | Purpose | Key Data Included |
|---|---|---|
| `ChipTypeResponse` | Details of a board variant | `id`, `name`, `description`, `flashSize`, `deviceCount`, `createdAt` |
| `DeviceGroupResponse` | Details of a device group | `id`, `name`, `description`, `deviceCount`, `createdAt`, `updatedAt` |
| `DeviceGroupSummaryResponse` | Lightweight group tag | `id`, `name` (embedded inside `DeviceResponse`) |
| `DeviceResponse` | Full status of an ESP32 node | `id`, `name`, `chipTypeId`, `chipTypeName`, `macAddress`, `apiKey`, `currentFirmwareVersion`, `status`, `lastSeenAt`, `groups` |
| `FirmwareImageResponse` | Metadata for uploaded firmware | `id`, `version`, `chipTypeName`, `fileName`, `fileSize`, `checksum`, `signature`, `isGeneric`, `description` (safe: no local file paths) |
| `UpdateAssignmentResponse` | Target assignment status | `id`, target device/group names, `firmwareVersion`, `chipTypeName`, `status`, timestamps |
| `UpdateHistoryResponse` | Individual update audit entry | `id`, device name, version transition (`previousVersion` → `newVersion`), `status`, `errorMessage`, durations |

---

## 7. Mapping Components (`mapper`)

**Package:** `com.juneth.ota_updates_backend.mapper`  
Spring `@Component` beans providing clean, bidirectional transformation between database Entities and DTO records.

### `ChipTypeMapper.java`
- `toEntity(CreateChipTypeRequest)`: Converts request into new `ChipType` entity.
- `updateEntity(ChipType, UpdateChipTypeRequest)`: Applies partial updates to an existing `ChipType`.
- `toResponse(ChipType)`: Converts entity to `ChipTypeResponse`, calculating the active device count.

### `DeviceGroupMapper.java`
- `toEntity(CreateDeviceGroupRequest)`: Converts request into new `DeviceGroup` entity.
- `updateEntity(DeviceGroup, UpdateDeviceGroupRequest)`: Applies name or description changes.
- `toResponse(DeviceGroup)`: Converts to `DeviceGroupResponse`, calculating the device count.
- `toSummary(DeviceGroup)`: Extracts lightweight `DeviceGroupSummaryResponse` (id and name).

### `DeviceMapper.java`
- `toEntity(CreateDeviceRequest, ChipType, Set<DeviceGroup>, apiKey)`: Constructs a new `Device` entity with associated chip type, security API key, and initial group memberships.
- `toResponse(Device)`: Transforms device into rich `DeviceResponse`, converting associated groups into `DeviceGroupSummaryResponse` items.

### `FirmwareImageMapper.java`
- `toEntity(UploadFirmwareMetadataRequest, ChipType, fileName, filePath, fileSize, checksum)`: Constructs `FirmwareImage` entity combining request metadata and binary storage attributes.
- `toResponse(FirmwareImage)`: Formats firmware metadata into `FirmwareImageResponse` without exposing backend storage paths.

### `UpdateAssignmentMapper.java`
- `toEntity(Device, DeviceGroup, FirmwareImage)`: Constructs a pending `UpdateAssignment`.
- `toResponse(UpdateAssignment)`: Transforms assignment into `UpdateAssignmentResponse` with target device or group names.

### `UpdateHistoryMapper.java`
- `toEntity(RecordUpdateProgressRequest, Device, UpdateAssignment, FirmwareImage)`: Records a progress/audit step in `UpdateHistory`.
- `toResponse(UpdateHistory)`: Transforms history entity into `UpdateHistoryResponse` for auditing and timeline views.

---

## Initial Board Groups Requirement

As configured for Stage 5+, the initial automatically created board groups corresponding to supported chip types are:
1. `NodeMCU-32S`
2. `ESP32-s3`
3. `ESP32-S3-SuperMini`
4. `ESP32-C3`
5. `ESP32-C3-SuperMini`

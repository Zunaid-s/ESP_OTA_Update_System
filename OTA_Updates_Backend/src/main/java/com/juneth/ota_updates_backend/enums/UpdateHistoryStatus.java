package com.juneth.ota_updates_backend.enums;

public enum UpdateHistoryStatus {
    STARTED,        // Update process initiated
    DOWNLOADING,    // Firmware binary being downloaded
    DOWNLOADED,     // Binary downloaded, ready to flash
    APPLYING,       // OTA flash in progress
    SUCCESS,        // Update completed successfully
    FAILED,         // Update failed during flash
    ROLLED_BACK     // Automatic rollback occurred
}

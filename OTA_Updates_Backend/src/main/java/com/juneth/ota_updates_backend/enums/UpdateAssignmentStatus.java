package com.juneth.ota_updates_backend.enums;

public enum UpdateAssignmentStatus {
    PENDING,       // Update queued, not yet sent to device
    IN_PROGRESS,   // Device is downloading/applying update
    COMPLETED,     // Update applied successfully
    FAILED,        // Update failed
    ROLLED_BACK    // Update was rolled back to previous version
}

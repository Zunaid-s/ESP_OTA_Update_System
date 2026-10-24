package com.juneth.ota_updates_backend.enums;

public enum DeviceStatus {
    UNPAIRED,    // New device, not yet registered
    PAIRING,     // In the process of pairing
    PAIRED,      // Successfully paired, ready for updates
    ACTIVE,      // Device is registered and receiving updates
    INACTIVE     // Device is offline or disabled
}

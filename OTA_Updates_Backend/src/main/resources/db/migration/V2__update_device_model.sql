-- V2: Update device model for many-to-many groups and add hardware metadata

-- 1. Add flash_size to chip_types
ALTER TABLE chip_types ADD COLUMN flash_size INTEGER;

-- 2. Add mac_address to devices
ALTER TABLE devices ADD COLUMN mac_address VARCHAR(17);

-- 3. Set placeholder MAC addresses for any existing rows to satisfy NOT NULL constraint
UPDATE devices SET mac_address = '00:00:00:00:00:00' WHERE mac_address IS NULL;

-- 4. Make mac_address NOT NULL and UNIQUE
ALTER TABLE devices ALTER COLUMN mac_address SET NOT NULL;
ALTER TABLE devices ADD CONSTRAINT uk_devices_mac_address UNIQUE (mac_address);

-- 5. Make firmware signature mandatory (nodes only accept signed updates)
ALTER TABLE firmware_images ALTER COLUMN signature SET NOT NULL;

-- 6. Create device_group_memberships join table
CREATE TABLE device_group_memberships (
    device_id UUID NOT NULL REFERENCES devices(id) ON DELETE CASCADE,
    group_id UUID NOT NULL REFERENCES device_groups(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (device_id, group_id)
);

-- 7. Migrate existing group relationships from devices table to join table
INSERT INTO device_group_memberships (device_id, group_id, created_at)
SELECT id, group_id, CURRENT_TIMESTAMP
FROM devices
WHERE group_id IS NOT NULL;

-- 8. Drop old foreign key constraint and column from devices
ALTER TABLE devices DROP CONSTRAINT IF EXISTS devices_group_id_fkey;
ALTER TABLE devices DROP COLUMN group_id;

-- 9. Cleanup old index
DROP INDEX IF EXISTS idx_devices_group_id;

-- 10. Create indexes for the join table
CREATE INDEX idx_device_group_memberships_device_id ON device_group_memberships(device_id);
CREATE INDEX idx_device_group_memberships_group_id ON device_group_memberships(group_id);

-- 11. Add comments for documentation
COMMENT ON COLUMN chip_types.flash_size IS 'Flash memory size in bytes for firmware size validation';
COMMENT ON COLUMN devices.mac_address IS 'MAC address (xx:xx:xx:xx:xx:xx) captured during device pairing';
COMMENT ON COLUMN firmware_images.signature IS 'Digital signature - MANDATORY for security, nodes reject unsigned firmware';
COMMENT ON TABLE device_group_memberships IS 'Many-to-many relationship between devices and device groups';

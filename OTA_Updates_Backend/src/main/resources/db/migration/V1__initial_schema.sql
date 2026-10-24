-- Enable UUID support
-- CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Chip Types Table
CREATE TABLE chip_types (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name VARCHAR(50) NOT NULL UNIQUE,
  description TEXT,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Device Groups Table
CREATE TABLE device_groups (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name VARCHAR(255) NOT NULL UNIQUE,
  description TEXT,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Devices Table
CREATE TABLE devices (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name VARCHAR(255) NOT NULL,
  chip_type_id UUID NOT NULL REFERENCES chip_types(id),
  group_id UUID REFERENCES device_groups(id),
  api_key VARCHAR(255) NOT NULL UNIQUE,
  current_firmware_version VARCHAR(100),
  status VARCHAR(50) NOT NULL DEFAULT 'UNPAIRED',
  last_seen_at TIMESTAMP,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Firmware Images Table
CREATE TABLE firmware_images (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  version VARCHAR(100) NOT NULL,
  chip_type_id UUID NOT NULL REFERENCES chip_types(id),
  file_name VARCHAR(255),
  file_path VARCHAR(500),
  file_size BIGINT,
  checksum VARCHAR(64),
  signature TEXT,
  is_generic BOOLEAN NOT NULL DEFAULT false,
  description TEXT,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Update Assignments Table
CREATE TABLE update_assignments (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  device_id UUID REFERENCES devices(id),
  group_id UUID REFERENCES device_groups(id),
  firmware_id UUID NOT NULL REFERENCES firmware_images(id),
  status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT at_least_one_target CHECK (device_id IS NOT NULL OR group_id IS NOT NULL)
);

-- Update History Table
CREATE TABLE update_history (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  device_id UUID NOT NULL REFERENCES devices(id),
  assignment_id UUID REFERENCES update_assignments(id),
  firmware_id UUID NOT NULL REFERENCES firmware_images(id),
  previous_version VARCHAR(100),
  new_version VARCHAR(100),
  status VARCHAR(50) NOT NULL DEFAULT 'STARTED',
  error_message TEXT,
  started_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  completed_at TIMESTAMP,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for common queries
CREATE INDEX idx_devices_group_id ON devices(group_id);
CREATE INDEX idx_devices_api_key ON devices(api_key);
CREATE INDEX idx_devices_chip_type_id ON devices(chip_type_id);
CREATE INDEX idx_firmware_chip_type_id ON firmware_images(chip_type_id);
CREATE INDEX idx_update_assignments_device_id ON update_assignments(device_id);
CREATE INDEX idx_update_assignments_group_id ON update_assignments(group_id);
CREATE INDEX idx_update_history_device_id ON update_history(device_id);

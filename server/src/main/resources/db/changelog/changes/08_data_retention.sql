-- liquibase formatted sql

-- changeset author:system:08_data_retention

-- Add retention_start_date column to users table
ALTER TABLE users ADD COLUMN retention_start_date TIMESTAMP;

-- Create data_deletion_audit table
CREATE TABLE data_deletion_audit (
    id UUID PRIMARY KEY,
    deletion_date TIMESTAMP NOT NULL,
    user_id UUID REFERENCES users(user_id),
    deletion_type VARCHAR(50) NOT NULL,
    affected_records JSONB,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Add indexes for efficient retention queries
CREATE INDEX idx_users_retention_start_date ON users(retention_start_date);
CREATE INDEX idx_data_deletion_audit_deletion_date ON data_deletion_audit(deletion_date);
CREATE INDEX idx_data_deletion_audit_user_id ON data_deletion_audit(user_id);

-- Update existing users to set retention_start_date to joined_at
UPDATE users SET retention_start_date = joined_at;

-- Once all existing data is migrated, make retention_start_date NOT NULL
ALTER TABLE users ALTER COLUMN retention_start_date SET NOT NULL;

-- Add data retention columns to users table
ALTER TABLE users
    ADD COLUMN last_activity_at TIMESTAMP,
    ADD COLUMN scheduled_deletion_at TIMESTAMP;

-- Backfill last_activity_at with most recent of updated_at or joined_at
UPDATE users
SET last_activity_at = GREATEST(updated_at, joined_at)
WHERE last_activity_at IS NULL;

-- Make last_activity_at non-nullable after backfill
ALTER TABLE users
    ALTER COLUMN last_activity_at SET NOT NULL;

-- Create audit log table for data retention
CREATE TABLE data_retention_audit_log (
    log_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    deletion_timestamp TIMESTAMP NOT NULL,
    deletion_reason VARCHAR(255) NOT NULL,
    deletion_status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
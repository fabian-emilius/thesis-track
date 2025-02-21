-- liquibase formatted sql

-- changeset gdpr:1
ALTER TABLE users
ADD COLUMN last_activity_date TIMESTAMP,
ADD COLUMN scheduled_deletion_date TIMESTAMP,
ADD COLUMN deletion_notified_at TIMESTAMP;

-- changeset gdpr:2
CREATE TABLE gdpr_deletion_log (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    deleted_at TIMESTAMP NOT NULL,
    reason TEXT NOT NULL,
    metadata JSONB
);

-- Add indexes for performance
-- changeset gdpr:3
CREATE INDEX idx_users_last_activity ON users(last_activity_date);
CREATE INDEX idx_users_scheduled_deletion ON users(scheduled_deletion_date);
CREATE INDEX idx_gdpr_deletion_log_user ON gdpr_deletion_log(user_id);
CREATE INDEX idx_gdpr_deletion_log_deleted_at ON gdpr_deletion_log(deleted_at);
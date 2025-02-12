-- liquibase formatted sql

-- changeset thesis:9
-- Migrate existing data to default group

-- Create default group
INSERT INTO groups (id, name, slug, description, created_at, updated_at)
VALUES (
    '00000000-0000-0000-0000-000000000001',
    'Default Research Group',
    'default-group',
    'Default research group for existing data',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Update existing topics
UPDATE topics
SET group_id = '00000000-0000-0000-0000-000000000001'
WHERE group_id IS NULL;

-- Update existing theses
UPDATE theses
SET group_id = '00000000-0000-0000-0000-000000000001'
WHERE group_id IS NULL;

-- Update existing applications
UPDATE applications
SET group_id = '00000000-0000-0000-0000-000000000001'
WHERE group_id IS NULL;

-- Create group members for existing users with roles
INSERT INTO group_members (group_id, user_id, role, created_at, updated_at)
SELECT DISTINCT
    '00000000-0000-0000-0000-000000000001',
    u.id,
    CASE
        WHEN u.role = 'SUPERVISOR' THEN 'SUPERVISOR'::varchar
        WHEN u.role = 'ADVISOR' THEN 'ADVISOR'::varchar
        ELSE 'ADVISOR'::varchar
    END,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM users u
WHERE u.role IN ('SUPERVISOR', 'ADVISOR');

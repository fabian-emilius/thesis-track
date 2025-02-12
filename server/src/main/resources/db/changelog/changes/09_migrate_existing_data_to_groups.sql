-- liquibase formatted sql

-- changeset thesis:9
-- Delete orphaned data without group association

DELETE FROM topics WHERE group_id IS NULL;
DELETE FROM theses WHERE group_id IS NULL;
DELETE FROM applications WHERE group_id IS NULL;

-- Create group members for existing users with roles
INSERT INTO group_members (group_id, user_id, role, is_admin, created_at, updated_at)
SELECT DISTINCT
    g.id,
    u.id,
    CASE
        WHEN u.role = 'SUPERVISOR' THEN 'SUPERVISOR'::varchar
        WHEN u.role = 'ADVISOR' THEN 'ADVISOR'::varchar
        ELSE 'ADVISOR'::varchar
    END,
    CASE
        WHEN u.role = 'SUPERVISOR' THEN true
        ELSE false
    END,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM users u
CROSS JOIN groups g
WHERE u.role IN ('SUPERVISOR', 'ADVISOR');

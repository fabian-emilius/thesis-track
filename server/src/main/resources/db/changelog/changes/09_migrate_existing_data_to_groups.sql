-- Create default group for existing data
INSERT INTO groups (id, name, slug, description, created_at, updated_at)
SELECT 
    '00000000-0000-0000-0000-000000000001'::uuid,
    'Default Group',
    'default-group',
    'Default group for existing data',
    NOW(),
    NOW()
WHERE NOT EXISTS (SELECT 1 FROM groups WHERE id = '00000000-0000-0000-0000-000000000001'::uuid);

-- Migrate existing topics to default group
UPDATE topics
SET group_id = '00000000-0000-0000-0000-000000000001'::uuid
WHERE group_id IS NULL;

-- Migrate existing applications to default group
UPDATE applications
SET group_id = '00000000-0000-0000-0000-000000000001'::uuid
WHERE group_id IS NULL;

-- Migrate existing user roles to group roles
INSERT INTO user_groups (user_id, group_id, role)
SELECT DISTINCT
    u.id,
    '00000000-0000-0000-0000-000000000001'::uuid,
    CASE
        WHEN u.role = 'ROLE_ADMIN' THEN 'admin'
        WHEN u.role = 'ROLE_SUPERVISOR' THEN 'supervisor'
        WHEN u.role = 'ROLE_ADVISOR' THEN 'advisor'
        ELSE 'member'
    END
FROM users u
WHERE NOT EXISTS (
    SELECT 1 FROM user_groups ug
    WHERE ug.user_id = u.id
    AND ug.group_id = '00000000-0000-0000-0000-000000000001'::uuid
);
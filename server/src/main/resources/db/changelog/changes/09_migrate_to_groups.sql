-- Create default group
INSERT INTO groups (id, slug, name, description, created_at, updated_at)
VALUES (
    '00000000-0000-0000-0000-000000000001',
    'default',
    'Default Group',
    'Default group for migrated data',
    NOW(),
    NOW()
);

-- Create default group settings
INSERT INTO group_settings (group_id)
VALUES ('00000000-0000-0000-0000-000000000001');

-- Migrate existing topics to default group
UPDATE topics
SET group_id = '00000000-0000-0000-0000-000000000001'
WHERE group_id IS NULL;

-- Migrate existing theses to default group
UPDATE theses
SET group_id = '00000000-0000-0000-0000-000000000001'
WHERE group_id IS NULL;

-- Make group_id NOT NULL after migration
ALTER TABLE topics ALTER COLUMN group_id SET NOT NULL;
ALTER TABLE theses ALTER COLUMN group_id SET NOT NULL;

-- Migrate existing users to group members based on their roles
INSERT INTO group_members (group_id, user_id, role)
SELECT DISTINCT
    '00000000-0000-0000-0000-000000000001',
    u.id,
    CASE
        WHEN EXISTS (SELECT 1 FROM user_groups ug WHERE ug.user_id = u.id AND ug."group" = 'admin') THEN 'GROUP_ADMIN'
        WHEN EXISTS (SELECT 1 FROM user_groups ug WHERE ug.user_id = u.id AND ug."group" = 'supervisor') THEN 'SUPERVISOR'
        WHEN EXISTS (SELECT 1 FROM user_groups ug WHERE ug.user_id = u.id AND ug."group" = 'advisor') THEN 'ADVISOR'
    END
FROM users u
WHERE EXISTS (
    SELECT 1 FROM user_groups ug 
    WHERE ug.user_id = u.id 
    AND ug."group" IN ('admin', 'supervisor', 'advisor')
);
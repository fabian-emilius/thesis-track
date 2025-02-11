-- Create groups table
CREATE TABLE groups (
    group_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Add index on group name
CREATE INDEX idx_groups_name ON groups(name);

-- Add group_id to topics table
ALTER TABLE topics
    ADD COLUMN group_id UUID REFERENCES groups(group_id);

-- Create default group
INSERT INTO groups (group_id, name, description)
VALUES ('00000000-0000-0000-0000-000000000001', 'Default Group', 'Default group for existing topics');

-- Migrate existing topics to default group
UPDATE topics
SET group_id = '00000000-0000-0000-0000-000000000001'
WHERE group_id IS NULL;

-- Make group_id non-nullable
ALTER TABLE topics
    ALTER COLUMN group_id SET NOT NULL;

-- Create user_groups table for many-to-many relationship
CREATE TABLE user_groups (
    user_id UUID NOT NULL REFERENCES users(user_id),
    group_id UUID NOT NULL REFERENCES groups(group_id),
    role TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, group_id)
);

-- Add indexes
CREATE INDEX idx_user_groups_user_id ON user_groups(user_id);
CREATE INDEX idx_user_groups_group_id ON user_groups(group_id);

-- Migrate existing users to default group
INSERT INTO user_groups (user_id, group_id, role)
SELECT DISTINCT u.user_id, '00000000-0000-0000-0000-000000000001', 'MEMBER'
FROM users u;
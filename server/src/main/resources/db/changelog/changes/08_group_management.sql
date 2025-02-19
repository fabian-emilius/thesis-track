-- liquibase formatted sql

-- changeset fabian:group-management-1
CREATE TABLE research_groups (
    id UUID PRIMARY KEY,
    slug VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    logo_filename VARCHAR(255),
    website_link VARCHAR(512),
    mail_footer TEXT,
    acceptance_email_text TEXT,
    acceptance_instructions TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_research_groups_slug ON research_groups(slug);

-- changeset fabian:group-management-2
CREATE TYPE group_role AS ENUM ('SUPERVISOR', 'ADVISOR', 'GROUP_ADMIN');

CREATE TABLE group_members (
    group_id UUID NOT NULL REFERENCES research_groups(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role group_role NOT NULL,
    joined_at TIMESTAMP WITH TIME ZONE NOT NULL,
    PRIMARY KEY (group_id, user_id)
);

CREATE INDEX idx_group_members_user ON group_members(user_id);

-- changeset fabian:group-management-3
ALTER TABLE topics 
ADD COLUMN group_id UUID REFERENCES research_groups(id) ON DELETE CASCADE;

ALTER TABLE applications
ADD COLUMN group_id UUID REFERENCES research_groups(id) ON DELETE CASCADE;

CREATE INDEX idx_topics_group ON topics(group_id);
CREATE INDEX idx_applications_group ON applications(group_id);

-- changeset fabian:group-management-4
-- Create default group for existing data
INSERT INTO research_groups (
    id, 
    slug,
    name,
    description,
    created_at,
    updated_at
) VALUES (
    '00000000-0000-0000-0000-000000000001',
    'default',
    'Default Research Group',
    'Default research group for migrated data',
    NOW(),
    NOW()
);

-- Migrate existing topics and applications
UPDATE topics SET group_id = '00000000-0000-0000-0000-000000000001'
WHERE group_id IS NULL;

UPDATE applications SET group_id = '00000000-0000-0000-0000-000000000001'
WHERE group_id IS NULL;

-- Make group_id required after migration
ALTER TABLE topics 
ALTER COLUMN group_id SET NOT NULL;

ALTER TABLE applications
ALTER COLUMN group_id SET NOT NULL;

-- changeset fabian:group-management-5
-- Migrate existing advisors and supervisors to group members
INSERT INTO group_members (group_id, user_id, role, joined_at)
SELECT 
    '00000000-0000-0000-0000-000000000001',
    u.id,
    CASE 
        WHEN ug.group_name = 'supervisor' THEN 'SUPERVISOR'::group_role
        WHEN ug.group_name = 'advisor' THEN 'ADVISOR'::group_role
    END,
    NOW()
FROM users u
JOIN user_groups ug ON u.id = ug.user_id
WHERE ug.group_name IN ('supervisor', 'advisor')
ON CONFLICT DO NOTHING;

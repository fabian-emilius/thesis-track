-- liquibase formatted sql

-- changeset fabian:08_multi_group_support
CREATE TABLE groups (
    id UUID PRIMARY KEY,
    name TEXT NOT NULL,
    slug TEXT NOT NULL UNIQUE,
    description TEXT,
    logo_url TEXT,
    website_url TEXT,
    settings JSONB,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_groups_slug ON groups(slug);

ALTER TABLE user_groups
ADD COLUMN role TEXT NOT NULL DEFAULT 'MEMBER';

ALTER TABLE topics
ADD COLUMN group_id UUID REFERENCES groups(id);

ALTER TABLE applications
ADD COLUMN group_id UUID REFERENCES groups(id);

CREATE INDEX idx_topics_group_id ON topics(group_id);
CREATE INDEX idx_applications_group_id ON applications(group_id);

-- Create initial group for existing data
INSERT INTO groups (
    id,
    name,
    slug,
    description,
    created_at,
    updated_at
)
VALUES (
    '00000000-0000-0000-0000-000000000001',
    'Default Group',
    'default',
    'Default group for existing data',
    NOW(),
    NOW()
);

-- Migrate existing data to default group
UPDATE topics SET group_id = '00000000-0000-0000-0000-000000000001';
UPDATE applications SET group_id = '00000000-0000-0000-0000-000000000001';

-- Make group_id NOT NULL after migration
ALTER TABLE topics
ALTER COLUMN group_id SET NOT NULL;

ALTER TABLE applications
ALTER COLUMN group_id SET NOT NULL;
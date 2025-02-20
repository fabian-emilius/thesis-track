-- Add groups table
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

-- Create index on slug for faster lookups
CREATE INDEX idx_groups_slug ON groups(slug);

-- Add role column to user_groups
ALTER TABLE user_groups
ADD COLUMN role TEXT NOT NULL;

-- Add group_id to topics
ALTER TABLE topics
ADD COLUMN group_id UUID REFERENCES groups(id);

-- Add index on topics group_id
CREATE INDEX idx_topics_group_id ON topics(group_id);

-- Add group_id to applications
ALTER TABLE applications
ADD COLUMN group_id UUID REFERENCES groups(id);

-- Add index on applications group_id
CREATE INDEX idx_applications_group_id ON applications(group_id);

-- Add group_id to theses
ALTER TABLE theses
ADD COLUMN group_id UUID REFERENCES groups(id);

-- Add index on theses group_id
CREATE INDEX idx_theses_group_id ON theses(group_id);

-- Create initial group for existing data
INSERT INTO groups (
    id,
    name,
    slug,
    description,
    settings,
    created_at,
    updated_at
)
VALUES (
    '00000000-0000-0000-0000-000000000001',
    'Technical University of Munich',
    'tum',
    'Default group for existing TUM data',
    '{}',
    NOW(),
    NOW()
);

-- Associate existing topics with default group
UPDATE topics SET group_id = '00000000-0000-0000-0000-000000000001';

-- Associate existing applications with default group
UPDATE applications SET group_id = '00000000-0000-0000-0000-000000000001';

-- Associate existing theses with default group
UPDATE theses SET group_id = '00000000-0000-0000-0000-000000000001';

-- Make group_id NOT NULL after data migration
ALTER TABLE topics
ALTER COLUMN group_id SET NOT NULL;

ALTER TABLE applications
ALTER COLUMN group_id SET NOT NULL;

ALTER TABLE theses
ALTER COLUMN group_id SET NOT NULL;
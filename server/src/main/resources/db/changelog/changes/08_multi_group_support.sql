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

-- Create index for group slug lookups
CREATE INDEX idx_groups_slug ON groups(slug);

-- Add role column to user_groups
ALTER TABLE user_groups
ADD COLUMN role TEXT NOT NULL;

-- Add group_id to topics
ALTER TABLE topics
ADD COLUMN group_id UUID REFERENCES groups(id);

-- Add index for group-based topic queries
CREATE INDEX idx_topics_group_id ON topics(group_id);

-- Add group_id to applications
ALTER TABLE applications
ADD COLUMN group_id UUID REFERENCES groups(id);

-- Add index for group-based application queries
CREATE INDEX idx_applications_group_id ON applications(group_id);

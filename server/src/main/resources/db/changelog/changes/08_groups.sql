-- Add groups table and related changes
CREATE TABLE groups (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Add group_id to topics
ALTER TABLE topics ADD COLUMN group_id UUID REFERENCES groups(id);

-- Add visibility_groups to published_theses
ALTER TABLE published_theses ADD COLUMN visibility_groups UUID[];

-- Create index for group lookups
CREATE INDEX idx_topics_group_id ON topics(group_id);

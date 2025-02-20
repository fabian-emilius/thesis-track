-- Create groups table
CREATE TABLE groups (
    id UUID PRIMARY KEY,
    slug VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    logo_url VARCHAR(255),
    external_link VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Create group settings table
CREATE TABLE group_settings (
    group_id UUID PRIMARY KEY REFERENCES groups(id),
    acceptance_email_template TEXT,
    post_acceptance_instructions TEXT,
    email_footer TEXT
);

-- Create group members table
CREATE TABLE group_members (
    group_id UUID REFERENCES groups(id),
    user_id UUID REFERENCES users(id),
    role VARCHAR(50) NOT NULL,
    PRIMARY KEY (group_id, user_id)
);

-- Add group_id to topics and theses
ALTER TABLE topics ADD COLUMN group_id UUID REFERENCES groups(id);
ALTER TABLE theses ADD COLUMN group_id UUID REFERENCES groups(id);

-- Create indexes for better performance
CREATE INDEX idx_group_members_user_id ON group_members(user_id);
CREATE INDEX idx_topics_group_id ON topics(group_id);
CREATE INDEX idx_theses_group_id ON theses(group_id);

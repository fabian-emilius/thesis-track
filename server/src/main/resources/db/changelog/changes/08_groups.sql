-- liquibase formatted sql

-- changeset thesis:8
-- Database schema for group support

-- Create groups table
CREATE TABLE groups (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    logo_path VARCHAR(255),
    link VARCHAR(255),
    mail_footer TEXT,
    acceptance_text TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create group_members table
CREATE TABLE group_members (
    group_id UUID NOT NULL REFERENCES groups(id),
    user_id UUID NOT NULL REFERENCES users(id),
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (group_id, user_id)
);

-- Add group_id to existing tables
ALTER TABLE topics ADD COLUMN group_id UUID REFERENCES groups(id);
ALTER TABLE theses ADD COLUMN group_id UUID REFERENCES groups(id);
ALTER TABLE applications ADD COLUMN group_id UUID REFERENCES groups(id);

-- Add indexes
CREATE INDEX idx_group_members_user_id ON group_members(user_id);
CREATE INDEX idx_topics_group_id ON topics(group_id);
CREATE INDEX idx_theses_group_id ON theses(group_id);
CREATE INDEX idx_applications_group_id ON applications(group_id);

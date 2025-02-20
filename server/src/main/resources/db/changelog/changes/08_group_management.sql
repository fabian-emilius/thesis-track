-- Create groups table
CREATE TABLE groups (
    id UUID PRIMARY KEY,
    slug VARCHAR(255) UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    logo_url VARCHAR(255),
    external_link VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
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

-- Create group audit logs table
CREATE TABLE group_audit_logs (
    id UUID PRIMARY KEY,
    group_id UUID NOT NULL REFERENCES groups(id),
    user_id UUID NOT NULL REFERENCES users(id),
    action VARCHAR(255) NOT NULL,
    details TEXT,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Add group_id to topics and theses
ALTER TABLE topics ADD COLUMN group_id UUID REFERENCES groups(id);
ALTER TABLE theses ADD COLUMN group_id UUID REFERENCES groups(id);

-- Create default group for existing data
INSERT INTO groups (id, slug, name, description, created_at, updated_at)
VALUES ('00000000-0000-0000-0000-000000000001', 'default', 'Default Group', 'Default group for existing data', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Create default group settings
INSERT INTO group_settings (group_id)
VALUES ('00000000-0000-0000-0000-000000000001');

-- Migrate existing topics and theses to default group
UPDATE topics SET group_id = '00000000-0000-0000-0000-000000000001' WHERE group_id IS NULL;
UPDATE theses SET group_id = '00000000-0000-0000-0000-000000000001' WHERE group_id IS NULL;

-- Add not null constraints after migration
ALTER TABLE topics ALTER COLUMN group_id SET NOT NULL;
ALTER TABLE theses ALTER COLUMN group_id SET NOT NULL;

-- Create indexes for performance
CREATE INDEX idx_group_members_user_id ON group_members(user_id);
CREATE INDEX idx_topics_group_id ON topics(group_id);
CREATE INDEX idx_theses_group_id ON theses(group_id);
CREATE INDEX idx_group_audit_logs_group_id ON group_audit_logs(group_id);
CREATE INDEX idx_group_audit_logs_user_id ON group_audit_logs(user_id);
CREATE INDEX idx_group_audit_logs_timestamp ON group_audit_logs(timestamp);

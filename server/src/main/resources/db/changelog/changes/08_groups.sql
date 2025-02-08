-- Create groups table
CREATE TABLE groups (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(1000),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Add group_id to topics table
ALTER TABLE topics
ADD COLUMN group_id BIGINT;

ALTER TABLE topics
ADD CONSTRAINT fk_topics_group
FOREIGN KEY (group_id)
REFERENCES groups(id);

-- Add group_id to theses table
ALTER TABLE theses
ADD COLUMN group_id BIGINT;

ALTER TABLE theses
ADD CONSTRAINT fk_theses_group
FOREIGN KEY (group_id)
REFERENCES groups(id);

-- Create index for better performance
CREATE INDEX idx_topics_group_id ON topics(group_id);
CREATE INDEX idx_theses_group_id ON theses(group_id);
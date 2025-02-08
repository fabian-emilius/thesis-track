-- Add group_id to applications table
ALTER TABLE applications
ADD COLUMN group_id BIGINT;

ALTER TABLE applications
ADD CONSTRAINT fk_applications_group
FOREIGN KEY (group_id)
REFERENCES groups(id);

-- Create index for better performance
CREATE INDEX idx_applications_group_id ON applications(group_id);

-- Update existing applications to use group from their topics
UPDATE applications a
SET group_id = t.group_id
FROM topics t
WHERE a.topic_id = t.topic_id;

-- Make group_id not nullable after migration
ALTER TABLE applications
ALTER COLUMN group_id SET NOT NULL;
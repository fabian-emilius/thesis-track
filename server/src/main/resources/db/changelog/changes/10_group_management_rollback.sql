-- Rollback script for group management changes

-- Remove group_id constraints
ALTER TABLE theses DROP CONSTRAINT theses_group_id_fkey;
ALTER TABLE topics DROP CONSTRAINT topics_group_id_fkey;

-- Drop indexes
DROP INDEX IF EXISTS idx_group_members_user_id;
DROP INDEX IF EXISTS idx_topics_group_id;
DROP INDEX IF EXISTS idx_theses_group_id;

-- Drop tables in correct order
DROP TABLE IF EXISTS group_members;
DROP TABLE IF EXISTS group_settings;
DROP TABLE IF EXISTS groups;

-- Remove group_id columns
ALTER TABLE topics DROP COLUMN group_id;
ALTER TABLE theses DROP COLUMN group_id;
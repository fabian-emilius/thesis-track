-- Add indexes to improve performance of data retention queries
CREATE INDEX IF NOT EXISTS idx_user_joined_at ON users(joined_at);
CREATE INDEX IF NOT EXISTS idx_user_updated_at ON users(updated_at);
CREATE INDEX IF NOT EXISTS idx_application_created_at ON applications(created_at);
CREATE INDEX IF NOT EXISTS idx_thesis_role_assigned_at ON thesis_roles(assigned_at);

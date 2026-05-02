ALTER TABLE spaces
    ADD COLUMN IF NOT EXISTS last_activity_at timestamp null;

CREATE INDEX IF NOT EXISTS idx_spaces_last_activity_at
    ON spaces(last_activity_at desc);
ALTER TABLE groups
    ADD COLUMN IF NOT EXISTS last_activity_at timestamp null;

CREATE INDEX IF NOT EXISTS idx_groups_last_activity_at
    ON groups(last_activity_at desc);


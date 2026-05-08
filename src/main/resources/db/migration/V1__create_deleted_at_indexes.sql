CREATE INDEX idx_notes_creator_deleted_at_created ON notes(creator_id, deleted_at, created_at DESC);


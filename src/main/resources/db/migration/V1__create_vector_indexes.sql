




--index for note search by embedding , ex: ORDER BY embedding <=> :vector 
-- vector search
CREATE INDEX idx_notes_embedding_active
ON notes
USING hnsw (embedding vector_cosine_ops)
WHERE deleted_at IS NULL;

-- tree structure
CREATE INDEX idx_notes_parent_id
ON notes(parent_id);

-- soft delete filter
CREATE INDEX idx_notes_deleted_at
ON notes(deleted_at);
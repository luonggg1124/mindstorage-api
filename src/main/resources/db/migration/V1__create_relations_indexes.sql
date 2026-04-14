-- index query
CREATE INDEX idx_notes_topic_id ON notes(topic_id);
CREATE INDEX idx_topics_group_id ON topics(group_id);

CREATE INDEX idx_groups_space_id ON groups(space_id);

CREATE INDEX idx_space_members_space_id ON space_members(space_id);
CREATE INDEX idx_space_members_user_id ON space_members(user_id);

CREATE UNIQUE INDEX idx_space_members_unique ON space_members(space_id, user_id);
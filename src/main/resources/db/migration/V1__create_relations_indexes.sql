-- index query
CREATE INDEX idx_notes_topic_id ON notes(topic_id);
CREATE INDEX idx_topics_group_id ON topics(group_id);

CREATE INDEX idx_groups_space_id ON groups(space_id);

CREATE INDEX idx_space_members_space_id ON space_members(space_id);
CREATE INDEX idx_space_members_user_id ON space_members(user_id);

CREATE UNIQUE INDEX idx_space_members_unique ON space_members(space_id, user_id);


CREATE INDEX idx_followers_follower_id ON followers(follower_id);
CREATE INDEX idx_followers_following_id ON followers(following_id);


CREATE UNIQUE INDEX idx_followers_unique ON followers(follower_id, following_id);

CREATE UNIQUE INDEX uk_invite_pending ON invitations(invitee_id, entity_id,entity_type) WHERE status = 'PENDING';

CREATE INDEX idx_invite_entity ON invitations(entity_id, entity_type);

CREATE INDEX idx_notifications_user_created ON notifications(user_id, created_at DESC);

CREATE INDEX idx_notifications_user_unread ON notifications(user_id, is_read);

CREATE INDEX idx_notifications_data_gin ON notifications USING GIN (data);


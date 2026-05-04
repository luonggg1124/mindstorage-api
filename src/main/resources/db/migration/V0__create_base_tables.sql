-- Base tables needed by later V1/V3... index migrations.
-- This project currently relies on Hibernate ddl-auto=update in dev, but Flyway
-- migrations already assume tables exist (they create indexes). For a fresh DB,
-- create the minimal schema here so the app can start and queries won't fail.

CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE IF NOT EXISTS users (
    id              BIGSERIAL PRIMARY KEY,
    username        TEXT NOT NULL,
    email           TEXT NOT NULL,
    password        TEXT NULL,
    full_name       TEXT NOT NULL,
    is_verified     BOOLEAN NOT NULL DEFAULT FALSE,
    avatar_file_key TEXT NULL,
    provider_id     TEXT NULL,
    provider_name   TEXT NULL,
    gender          TEXT NULL,
    hobbies         TEXT NULL,
    intended_use    TEXT NULL,
    created_at      TIMESTAMP NULL,
    updated_at      TIMESTAMP NULL
);

CREATE TABLE IF NOT EXISTS spaces (
    id               UUID PRIMARY KEY,
    name             TEXT NOT NULL,
    description      TEXT NULL,
    image_file_key   TEXT NULL,
    deleted_at       TIMESTAMP NULL,
    visibility       TEXT NOT NULL DEFAULT 'PUBLIC',
    visibility_role  TEXT NOT NULL DEFAULT 'VIEWER',
    creator_id       BIGINT NULL,
    last_activity_at TIMESTAMP NULL,
    deleted_by       BIGINT NULL,
    created_at       TIMESTAMP NULL,
    updated_at       TIMESTAMP NULL
);

CREATE TABLE IF NOT EXISTS groups (
    id               UUID PRIMARY KEY,
    name             TEXT NOT NULL,
    description      TEXT NULL,
    deleted_at       TIMESTAMP NULL,
    space_id         UUID NULL,
    last_activity_at TIMESTAMP NULL,
    deleted_by       BIGINT NULL,
    created_at       TIMESTAMP NULL,
    updated_at       TIMESTAMP NULL
);

CREATE TABLE IF NOT EXISTS topics (
    id         UUID PRIMARY KEY,
    name       TEXT NOT NULL,
    group_id   UUID NULL,
    deleted_at TIMESTAMP NULL,
    deleted_by BIGINT NULL,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL
);

CREATE TABLE IF NOT EXISTS notes (
    id         UUID PRIMARY KEY,
    title      TEXT NOT NULL,
    content    TEXT NULL,
    deleted_at TIMESTAMP NULL,
    embedding  vector(384) NULL,
    topic_id   UUID NULL,
    parent_id  UUID NULL,
    creator_id BIGINT NULL,
    deleted_by BIGINT NULL,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL
);

CREATE TABLE IF NOT EXISTS space_members (
    id         UUID PRIMARY KEY,
    space_id   UUID NOT NULL,
    user_id    BIGINT NOT NULL,
    role       TEXT NOT NULL,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL
);

CREATE TABLE IF NOT EXISTS invitations (
    id           UUID PRIMARY KEY,
    inviter_id   BIGINT NOT NULL,
    invitee_id   BIGINT NOT NULL,
    entity_id    UUID NOT NULL,
    entity_type  TEXT NULL,
    type         TEXT NOT NULL,
    status       TEXT NOT NULL,
    created_at   TIMESTAMP NOT NULL,
    responded_at TIMESTAMP NULL
);

CREATE TABLE IF NOT EXISTS notifications (
    id         UUID PRIMARY KEY,
    user_id    BIGINT NOT NULL,
    sender_id  BIGINT NOT NULL,
    data       JSONB NULL,
    is_read    BOOLEAN NOT NULL DEFAULT FALSE,
    read_at    TIMESTAMP NULL,
    deleted_at TIMESTAMP NULL,
    type       TEXT NOT NULL,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL
);

CREATE TABLE IF NOT EXISTS attachments (
    id            UUID PRIMARY KEY,
    file_key      TEXT NOT NULL,
    file_url      TEXT NOT NULL,
    original_name TEXT NOT NULL,
    mime_type     TEXT NOT NULL,
    file_size     BIGINT NOT NULL,
    creator_id    BIGINT NULL,
    created_at    TIMESTAMP NULL,
    updated_at    TIMESTAMP NULL
);


-- =============================================================
-- V19__add_fcm_token_to_users.sql
-- Stores each user's Firebase Cloud Messaging token
-- Flutter sends this token after login so backend can push alerts
-- =============================================================

ALTER TABLE users
    ADD COLUMN fcm_token      VARCHAR(500) NULL DEFAULT NULL AFTER is_email_verified,
    ADD COLUMN fcm_updated_at TIMESTAMP    NULL DEFAULT NULL AFTER fcm_token;

CREATE INDEX idx_users_fcm_token ON users (fcm_token);
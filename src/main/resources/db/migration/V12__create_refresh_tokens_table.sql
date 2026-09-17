-- =============================================================
-- V12__create_refresh_tokens_table.sql
-- =============================================================
-- Purpose:
--   Introduces a secure session management system using refresh tokens.
--
-- Architectural Role:
--   This table is part of the authentication and session layer.
--   It enables persistent login, token rotation, and session control.
--
-- System Responsibilities:
--   - Maintains long-lived authentication sessions
--   - Supports secure token-based login (JWT refresh flow)
--   - Enables multi-device session tracking
--   - Provides ability to revoke sessions manually or automatically
--   - Supports security auditing (device + IP tracking)
--
-- Security Model:
--   - Tokens are stored in hashed form (not raw tokens)
--   - Each token has strict expiry enforcement
--   - Revocation state is explicitly tracked
--   - Integrity constraints prevent inconsistent token states
--
-- Lifecycle Behavior:
--   - Tokens are created at login
--   - Tokens are validated during access token refresh
--   - Tokens expire automatically after defined duration
--   - Tokens can be revoked by user logout or admin action
--   - Expired/revoked tokens are logically ignored by system
--
-- Relationship Model:
--   - Each token belongs to exactly one user
--   - A user can have multiple active refresh tokens
--   - Tokens are tightly coupled to user lifecycle
--
-- Data Integrity Strategy:
--   - Ensures uniqueness of each token record
--   - Prevents invalid expiration/revocation states
--   - Enforces strict foreign key relationship to users table
--
-- Performance Strategy:
--   - Indexed for fast user session lookup
--   - Indexed for expiry cleanup operations
--   - Indexed for revocation filtering
--
-- Scalability Considerations:
--   - Designed to support multi-device logins
--   - Supports high-frequency authentication workflows
--   - Can be extended to distributed caching (e.g. Redis)
-- =============================================================


-- =============================================================
-- TABLE CREATION: REFRESH TOKENS
-- =============================================================
-- Creates a session persistence layer for authentication system.
--
-- This table acts as the backbone for:
--   - JWT refresh workflows
--   - session continuation after access token expiry
--   - secure logout and session invalidation
-- =============================================================
CREATE TABLE refresh_tokens (



    -- =========================================================
    -- INTERNAL RECORD IDENTITY
    -- =========================================================
    -- Provides unique identification for each token entry.
    --
    -- Used for:
    --   - database management
    --   - debugging
    --   - administrative queries
    -- =========================================================
                                id BIGINT NOT NULL AUTO_INCREMENT,



    -- =========================================================
    -- SESSION OWNERSHIP LINK
    -- =========================================================
    -- Connects each refresh token to a specific user account.
    --
    -- Ensures:
    --   - session-user binding
    --   - multi-session support per user
    --   - secure authentication mapping
    -- =========================================================
                                user_id BIGINT NOT NULL,



    -- =========================================================
    -- TOKEN SECURITY LAYER
    -- =========================================================
    -- Stores hashed representation of refresh tokens.
    --
    -- Design Intent:
    --   - prevent exposure of raw tokens
    --   - mitigate database breach risks
    --   - enforce secure session validation
    -- =========================================================
                                token_hash VARCHAR(255) NOT NULL,



    -- =========================================================
    -- DEVICE & NETWORK TRACKING LAYER
    -- =========================================================
    -- Captures contextual metadata for each session.
    --
    -- Used for:
    --   - detecting unusual login behavior
    --   - auditing session origin
    --   - improving user security visibility
    -- =========================================================
                                device_info VARCHAR(500) NULL DEFAULT NULL,
                                ip_address VARCHAR(45) NULL DEFAULT NULL,



    -- =========================================================
    -- TOKEN LIFECYCLE CONTROL
    -- =========================================================
    -- Defines validity window for refresh tokens.
    --
    -- System Behavior:
    --   - tokens become invalid after expiry
    --   - expired tokens are ignored in authentication flow
    -- =========================================================
                                expires_at TIMESTAMP NOT NULL,



    -- =========================================================
    -- REVOCATION SYSTEM
    -- =========================================================
    -- Provides manual and automatic session invalidation.
    --
    -- Use Cases:
    --   - user logout
    --   - security breach response
    --   - admin forced logout
    --
    -- Ensures tokens can be invalidated before expiry.
    -- =========================================================
                                is_revoked BOOLEAN NOT NULL DEFAULT FALSE,
                                revoked_at TIMESTAMP NULL DEFAULT NULL,
                                revoke_reason VARCHAR(255) NULL DEFAULT NULL,



    -- =========================================================
    -- AUDIT TRAIL
    -- =========================================================
    -- Tracks when session was created.
    --
    -- Used for:
    --   - session monitoring
    --   - security analysis
    --   - activity tracking
    -- =========================================================
                                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,



    -- =========================================================
    -- PRIMARY KEY DEFINITION
    -- =========================================================
    -- Ensures each refresh token record is uniquely identifiable.
    -- =========================================================
                                PRIMARY KEY (id),



    -- =========================================================
    -- UNIQUENESS CONSTRAINT
    -- =========================================================
    -- Ensures no duplicate refresh tokens exist in the system.
    --
    -- Prevents:
    --   - token reuse attacks
    --   - duplication errors
    -- =========================================================
                                CONSTRAINT uq_refresh_token_hash UNIQUE (token_hash),



    -- =========================================================
    -- BUSINESS RULE VALIDATION
    -- =========================================================
    -- Ensures:
    --   - tokens cannot expire before creation
    --   - revoked state is always consistent
    --
    -- Prevents logical inconsistencies in session lifecycle.
    -- =========================================================
                                CONSTRAINT chk_token_expiry CHECK (expires_at > created_at),

                                CONSTRAINT chk_token_revoke CHECK (
                                    (is_revoked = FALSE AND revoked_at IS NULL) OR
                                    (is_revoked = TRUE  AND revoked_at IS NOT NULL)
                                    ),



    -- =========================================================
    -- FOREIGN KEY RELATIONSHIP
    -- =========================================================
    -- Establishes dependency on users table.
    --
    -- Behavior:
    --   ON UPDATE CASCADE:
    --       keeps user relationships synchronized
    --
    --   ON DELETE CASCADE:
    --       removes all sessions when user is deleted
    --
    -- Ensures no orphan session data exists.
    -- =========================================================
                                CONSTRAINT fk_refresh_user
                                    FOREIGN KEY (user_id) REFERENCES users (id)
                                        ON UPDATE CASCADE ON DELETE CASCADE
);



-- =============================================================
-- INDEXING SECTION
-- =============================================================
-- Optimizes authentication and session management queries.
--
-- Key Optimizations:
--   - fast lookup of user sessions
--   - efficient expiry cleanup operations
--   - quick filtering of active/revoked tokens
--
-- These indexes are critical for:
--   - login performance
--   - token refresh speed
--   - background cleanup jobs
-- =============================================================


-- =============================================================
-- USER SESSION LOOKUP INDEX
-- =============================================================
-- Speeds up retrieval of all sessions for a given user.
-- =============================================================
CREATE INDEX idx_refresh_user_id
    ON refresh_tokens (user_id);



-- =============================================================
-- EXPIRY MANAGEMENT INDEX
-- =============================================================
-- Enables fast cleanup of expired sessions.
-- =============================================================
CREATE INDEX idx_refresh_expires
    ON refresh_tokens (expires_at);



-- =============================================================
-- REVOCATION FILTER INDEX
-- =============================================================
-- Optimizes queries that filter active vs revoked tokens.
-- =============================================================
CREATE INDEX idx_refresh_is_revoked
    ON refresh_tokens (is_revoked);
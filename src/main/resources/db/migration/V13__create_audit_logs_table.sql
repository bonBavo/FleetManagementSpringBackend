-- =============================================================
-- V13__create_audit_logs_table.sql
-- =============================================================
-- Purpose:
--   Stores a complete, immutable audit trail of all system actions.
--
-- Architectural Role:
--   This is the system’s “black box recorder”.
--
--   It captures every important change happening in the platform:
--   - user actions
--   - admin operations
--   - system updates
--   - entity modifications
--
-- System Responsibilities:
--   - Records who did what, when, and where
--   - Tracks before/after state of data changes
--   - Provides forensic and security traceability
--   - Supports compliance and debugging requirements
--
-- Design Philosophy:
--   - Append-only (never updated or deleted)
--   - No foreign keys (survives user deletion)
--   - Fully independent of other system tables
--
-- Event Flow Role:
--   Application action → service layer → audit log entry → stored permanently
--
-- Business Role:
--   Enables:
--   - security auditing
--   - fraud detection
--   - system debugging
--   - compliance reporting
--   - accountability tracking
--
-- Data Design Strategy:
--   - Each row represents a single system action event
--   - Captures both state transitions and metadata context
--   - Stores structured JSON for flexible change tracking
--
-- Integrity Strategy:
--   - Immutable records (no updates allowed conceptually)
--   - No relational dependency to preserve historical accuracy
--   - Designed to survive user deletion or system changes
--
-- Performance Strategy:
--   - Indexed for actor-based queries
--   - Indexed for entity-based history tracking
--   - Optimized for chronological audit timelines
--
-- Scalability Considerations:
--   Designed for high-volume event logging systems
--   (enterprise-grade audit logging for distributed systems)
-- =============================================================


CREATE TABLE audit_logs (



    -- =========================================================
    -- PRIMARY IDENTIFIER
    -- =========================================================
    -- Unique ID for each audit event.
    -- =========================================================
                            id BIGINT NOT NULL AUTO_INCREMENT,



    -- =========================================================
    -- ACTOR INFORMATION
    -- =========================================================
    -- actor_id   → ID of user/system that performed action
    -- actor_role → role at time of action (ADMIN, DRIVER, etc.)
    --
    -- Note:
    -- actor_id is NOT a foreign key by design
    -- to ensure logs remain even after user deletion.
    -- =========================================================
                            actor_id BIGINT NULL DEFAULT NULL,
                            actor_role VARCHAR(50) NULL DEFAULT NULL,



    -- =========================================================
    -- ACTION DETAILS
    -- =========================================================
    -- action → type of operation performed (CREATE, UPDATE, DELETE)
    -- =========================================================
                            action VARCHAR(100) NOT NULL,



    -- =========================================================
    -- TARGET ENTITY INFORMATION
    -- =========================================================
    -- entity_type → type of object affected (vehicle, trip, etc.)
    -- entity_id   → ID of the affected record
    -- =========================================================
                            entity_type VARCHAR(100) NOT NULL,
                            entity_id BIGINT NULL DEFAULT NULL,



    -- =========================================================
    -- STATE TRANSITION DATA
    -- =========================================================
    -- old_value → data before change (JSON format)
    -- new_value → data after change (JSON format)
    --
    -- Used for full change tracking and rollback analysis.
    -- =========================================================
                            old_value JSON NULL DEFAULT NULL,
                            new_value JSON NULL DEFAULT NULL,



    -- =========================================================
    -- CONTEXT INFORMATION
    -- =========================================================
    -- ip_address → origin of request
    -- user_agent → device/browser information
    -- description → human-readable explanation of event
    -- =========================================================
                            ip_address VARCHAR(45) NULL DEFAULT NULL,
                            user_agent VARCHAR(500) NULL DEFAULT NULL,
                            description VARCHAR(1000) NULL DEFAULT NULL,



    -- =========================================================
    -- TIMESTAMP
    -- =========================================================
    -- created_at → time event was recorded
    --
    -- Note:
    -- No updated_at field exists because audit logs are immutable.
    -- =========================================================
                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,



    -- =========================================================
    -- PRIMARY KEY
    -- =========================================================
                            PRIMARY KEY (id)
);



-- =============================================================
-- INDEXING SECTION
-- =============================================================
-- Optimized for forensic queries and audit investigations.
-- =============================================================

CREATE INDEX idx_audit_actor_id
    ON audit_logs (actor_id);

CREATE INDEX idx_audit_entity_type
    ON audit_logs (entity_type);

CREATE INDEX idx_audit_entity_id
    ON audit_logs (entity_id);

CREATE INDEX idx_audit_action
    ON audit_logs (action);

CREATE INDEX idx_audit_created_at
    ON audit_logs (created_at);

CREATE INDEX idx_audit_entity_full
    ON audit_logs (entity_type, entity_id, created_at);
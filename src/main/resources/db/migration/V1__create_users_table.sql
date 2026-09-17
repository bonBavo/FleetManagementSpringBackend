-- =============================================================
-- V1__create_users_table.sql
-- =============================================================
-- Purpose:
--   Creates the foundational users table for the system.
--
-- Architecture Role:
--   This table acts as the identity and authentication core
--   of the application.
--
-- Design Principles:
--   - Follows normalized database structure (3NF)
--   - Separates authentication concerns from profile data
--   - Supports scalable role-based access control
--   - Supports auditing and lifecycle management
--   - Enables soft deletion instead of hard deletion
--
-- System Responsibilities:
--   - User authentication
--   - User authorization
--   - Account activation/deactivation
--   - Email verification tracking
--   - Suspension management
--   - Audit timestamp tracking
--
-- Security Considerations:
--   - Passwords are stored as hashes only
--   - Duplicate identities are prevented
--   - Validation constraints enforce data integrity
--
-- Performance Considerations:
--   - Frequently queried columns are indexed
--   - Lookup operations optimized for login/auth flows
--
-- Future Expansion:
--   - Additional profile information extracted into
--     separate tables to maintain normalization
-- =============================================================


-- =============================================================
-- TABLE CREATION
-- =============================================================
-- Creates the primary users table that stores
-- identity and access management data.
--
-- This becomes a parent table referenced by:
--   - vehicles
--   - trips
--   - audit logs
--   - notifications
--   - driver assignments
--   - permissions
-- =============================================================
CREATE TABLE users (

    -- =========================================================
    -- USER IDENTITY SECTION
    -- =========================================================
    -- Stores unique identity information required for:
    --   - authentication
    --   - communication
    --   - user recognition
    -- =========================================================

                       id                  BIGINT          NOT NULL AUTO_INCREMENT,
                       full_name           VARCHAR(150)    NOT NULL,
                       email               VARCHAR(255)    NOT NULL,
                       phone               VARCHAR(20)     NOT NULL,
                       password_hash       VARCHAR(255)    NOT NULL,



    -- =========================================================
    -- AUTHORIZATION SECTION
    -- =========================================================
    -- Controls system permissions and access levels.
    --
    -- Used by:
    --   - Spring Security
    --   - JWT authorization
    --   - Role-based access control (RBAC)
    --
    -- Defines what users are allowed to do
    -- throughout the platform.
    -- =========================================================
                       role                ENUM('SUPER_ADMIN','ADMIN','MANAGER','DRIVER')
                                        NOT NULL DEFAULT 'DRIVER',



    -- =========================================================
    -- ACCOUNT STATUS SECTION
    -- =========================================================
    -- Tracks the operational state of the account.
    --
    -- Used for:
    --   - enabling/disabling access
    --   - soft deletion
    --   - verification enforcement
    --
    -- Soft deletion preserves historical records
    -- and maintains referential integrity.
    -- =========================================================
                       is_active           BOOLEAN         NOT NULL DEFAULT TRUE,
                       is_deleted          BOOLEAN         NOT NULL DEFAULT FALSE,
                       is_email_verified   BOOLEAN         NOT NULL DEFAULT FALSE,



    -- =========================================================
    -- SUSPENSION MANAGEMENT SECTION
    -- =========================================================
    -- Handles temporary or permanent restriction
    -- of user access.
    --
    -- Used for:
    --   - policy violations
    --   - fraud prevention
    --   - security enforcement
    --   - administrative moderation
    -- =========================================================
                       suspended_at        TIMESTAMP       NULL DEFAULT NULL,
                       suspended_reason    VARCHAR(500)    NULL DEFAULT NULL,



    -- =========================================================
    -- AUDIT & LIFECYCLE TRACKING SECTION
    -- =========================================================
    -- Maintains operational history for:
    --   - account creation
    --   - account updates
    --   - deletion tracking
    --
    -- Essential for:
    --   - auditing
    --   - debugging
    --   - compliance
    --   - analytics
    -- =========================================================
                       created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

                       updated_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
                           ON UPDATE CURRENT_TIMESTAMP,

                       deleted_at          TIMESTAMP       NULL DEFAULT NULL,



    -- =========================================================
    -- PRIMARY KEY CONSTRAINT
    -- =========================================================
    -- Guarantees that each user record
    -- is uniquely identifiable.
    --
    -- Enables:
    --   - table relationships
    --   - foreign key references
    --   - optimized indexing
    -- =========================================================
                       PRIMARY KEY (id),



    -- =========================================================
    -- UNIQUENESS CONSTRAINTS
    -- =========================================================
    -- Prevents duplicate identities
    -- inside the system.
    --
    -- Ensures:
    --   - one account per email
    --   - one account per phone number
    --
    -- Critical for authentication integrity.
    -- =========================================================
                       CONSTRAINT uq_users_email   UNIQUE (email),
                       CONSTRAINT uq_users_phone   UNIQUE (phone),



    -- =========================================================
    -- DATA VALIDATION CONSTRAINTS
    -- =========================================================
    -- Enforces basic input integrity at
    -- the database level.
    --
    -- Prevents malformed or invalid data
    -- from entering the system even if:
    --   - frontend validation fails
    --   - backend validation is bypassed
    --   - APIs are abused
    --
    -- Acts as the final layer of defense
    -- for data quality.
    -- =========================================================
                       CONSTRAINT chk_users_email  CHECK  (email LIKE '%@%.%'),

                       CONSTRAINT chk_users_phone  CHECK  (CHAR_LENGTH(phone) >= 10),

                       CONSTRAINT chk_users_name   CHECK  (CHAR_LENGTH(TRIM(full_name)) > 0)
);



-- =============================================================
-- INDEXING SECTION
-- =============================================================
-- Creates indexes to optimize query performance.
--
-- These indexes improve:
--   - login operations
--   - authorization lookups
--   - filtering operations
--   - admin dashboard queries
--   - active/inactive user searches
--
-- Without indexes:
--   - full table scans occur
--   - performance degrades at scale
--
-- With indexes:
--   - lookups become significantly faster
--   - authentication latency decreases
--   - filtering becomes scalable
-- =============================================================


-- =============================================================
-- EMAIL LOOKUP INDEX
-- =============================================================
-- Optimizes:
--   - login queries
--   - email existence checks
--   - authentication lookups
--   - password reset operations
-- =============================================================
CREATE INDEX idx_users_email
    ON users (email);



-- =============================================================
-- ROLE FILTERING INDEX
-- =============================================================
-- Optimizes:
--   - RBAC queries
--   - admin dashboards
--   - permission filtering
--   - role-based reporting
-- =============================================================
CREATE INDEX idx_users_role
    ON users (role);



-- =============================================================
-- ACTIVE STATUS INDEX
-- =============================================================
-- Optimizes:
--   - active user filtering
--   - login eligibility checks
--   - operational user searches
-- =============================================================
CREATE INDEX idx_users_is_active
    ON users (is_active);



-- =============================================================
-- SOFT DELETE INDEX
-- =============================================================
-- Optimizes:
--   - exclusion of deleted accounts
--   - archival queries
--   - recovery operations
--   - operational filtering
-- =============================================================
CREATE INDEX idx_users_is_deleted
    ON users (is_deleted);
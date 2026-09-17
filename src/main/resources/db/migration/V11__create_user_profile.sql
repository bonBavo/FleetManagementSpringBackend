-- =============================================================
-- V2__create_user_profiles_table.sql
-- =============================================================
-- Purpose:
--   Introduces a dedicated profile table that extends user data
--   without mixing authentication concerns with personal details.
--
-- Architectural Role:
--   This table represents the "profile layer" of the system.
--   It is a dependent extension of the users table.
--
-- Design Pattern:
--   - Implements strict 1:1 relationship with users
--   - Enforces Third Normal Form (3NF)
--   - Separates mutable profile data from stable identity data
--
-- Lifecycle Strategy:
--   - Profile rows are created lazily (only when needed)
--   - Not automatically created at user registration
--
-- System Responsibility:
--   - Stores optional personal information
--   - Stores driver-specific attributes (only when applicable)
--   - Stores emergency contact information
--   - Stores user-facing profile content
--
-- Data Integrity Strategy:
--   - Enforces strict ownership mapping to users table
--   - Prevents duplicate profiles per user
--   - Ensures referential integrity via foreign keys
--
-- Deletion Strategy:
--   - Fully dependent on users table lifecycle
--   - Cascade deletion ensures no orphan profiles exist
-- =============================================================



-- =============================================================
-- TABLE CREATION: USER PROFILES
-- =============================================================
-- Creates a secondary table that extends the users table.
--
-- Relationship Type:
--   users (1)  →  user_profiles (1)
--
-- Meaning:
--   Each user can have exactly one profile row.
--   Each profile belongs to exactly one user.
--
-- Purpose in System:
--   Keeps authentication system lightweight while
--   allowing flexible user enrichment.
-- =============================================================
CREATE TABLE user_profiles (



    -- =========================================================
    -- PRIMARY IDENTITY OF PROFILE RECORD
    -- =========================================================
    -- Internal database identifier for profile rows.
    --
    -- Used for:
    --   - internal joins
    --   - debugging
    --   - administrative queries
    -- =========================================================
                               id BIGINT NOT NULL AUTO_INCREMENT,



    -- =========================================================
    -- OWNERSHIP LINK TO USERS TABLE
    -- =========================================================
    -- Core relational anchor of the entire table.
    --
    -- Ensures:
    --   - every profile belongs to a valid user
    --   - strict 1:1 relationship enforcement
    --
    -- This is the most critical structural dependency
    -- in the table.
    -- =========================================================
                               user_id BIGINT NOT NULL,



    -- =========================================================
    -- PROFILE INFORMATION LAYER
    -- =========================================================
    -- Contains optional user-facing personal data.
    --
    -- Design Intent:
    --   - separated from authentication data (3NF)
    --   - allows partial profile updates
    --   - avoids bloating users table
    -- =========================================================
                               profile_photo_url VARCHAR(500) NULL DEFAULT NULL,
                               date_of_birth DATE NULL DEFAULT NULL,
                               address_line VARCHAR(255) NULL DEFAULT NULL,
                               city VARCHAR(100) NULL DEFAULT NULL,
                               country VARCHAR(100) NULL DEFAULT NULL,



    -- =========================================================
    -- ROLE-SPECIFIC EXTENSION DATA (DRIVER MODULE)
    -- =========================================================
    -- Optional fields used only when user is a driver.
    --
    -- Design Reason:
    --   Avoids polluting core user table with role-specific data
    -- =========================================================
                               license_number VARCHAR(50) NULL DEFAULT NULL,
                               license_expiry DATE NULL DEFAULT NULL,



    -- =========================================================
    -- EMERGENCY CONTACT MODULE
    -- =========================================================
    -- Stores secondary contact information
    -- used for safety and operational emergencies.
    --
    -- Design Reason:
    --   Keeps sensitive contact relationships separate
    --   from authentication layer.
    -- =========================================================
                               emergency_contact VARCHAR(150) NULL DEFAULT NULL,
                               emergency_phone VARCHAR(20) NULL DEFAULT NULL,



    -- =========================================================
    -- USER BIO SECTION
    -- =========================================================
    -- Stores free-form descriptive text about the user.
    --
    -- Used in:
    --   - profiles
    --   - social features
    --   - admin review panels
    -- =========================================================
                               bio VARCHAR(1000) NULL DEFAULT NULL,



    -- =========================================================
    -- LIFECYCLE TRACKING
    -- =========================================================
    -- Tracks creation and modification time of profile data.
    --
    -- Used for:
    --   - auditing
    --   - synchronization
    --   - change tracking
    -- =========================================================
                               created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                               updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                                   ON UPDATE CURRENT_TIMESTAMP,



    -- =========================================================
    -- PRIMARY KEY DEFINITION
    -- =========================================================
    -- Uniquely identifies each profile record internally.
    -- =========================================================
                               PRIMARY KEY (id),



    -- =========================================================
    -- UNIQUENESS RULES
    -- =========================================================
    -- Enforces strict one-to-one mapping:
    --   - one profile per user
    --
    -- Also ensures:
    --   - license numbers are globally unique
    -- =========================================================
                               CONSTRAINT uq_user_profiles_user_id UNIQUE (user_id),
                               CONSTRAINT uq_user_profiles_license UNIQUE (license_number),



    -- =========================================================
    -- FOREIGN KEY RELATIONSHIP
    -- =========================================================
    -- Establishes dependency on users table.
    --
    -- Behavior rules:
    --   ON UPDATE CASCADE:
    --       If user ID changes, profile updates automatically
    --
    --   ON DELETE CASCADE:
    --       If user is deleted, profile is automatically removed
    --
    -- Ensures:
    --   - no orphan profile records
    --   - strong referential integrity
    -- =========================================================
                               CONSTRAINT fk_user_profiles_user
                                   FOREIGN KEY (user_id)
                                       REFERENCES users (id)
                                       ON UPDATE CASCADE
                                       ON DELETE CASCADE
);



-- =============================================================
-- INDEXING SECTION
-- =============================================================
-- Improves performance of:
--   - profile lookup by user
--   - joins between users and profiles
--   - dashboard/profile rendering
--
-- This is critical because:
--   user_id is used in almost every query involving profiles
-- =============================================================
CREATE INDEX idx_user_profiles_user_id
    ON user_profiles (user_id);
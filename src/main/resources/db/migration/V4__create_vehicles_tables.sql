-- =============================================================
-- V2__create_vehicles_table.sql
-- =============================================================
-- Purpose:
--   Defines the core fleet asset table of the system.
--   This table represents physical vehicles managed in the platform.
--
-- Architectural Role:
--   This is a central entity in the fleet management domain.
--   It connects users (owners) with standardized vehicle models.
--
-- Design Principles:
--   - Fully normalized design (3NF) using model reference table
--   - Avoids duplication of make/model data
--   - Uses soft deletion instead of hard delete
--   - Enforces strict ownership and identity constraints
--
-- System Responsibilities:
--   - Stores all registered vehicles in the system
--   - Tracks ownership and assignment of vehicles
--   - Maintains operational status of each vehicle
--   - Supports lifecycle management (active, maintenance, etc.)
--
-- Data Integrity Strategy:
--   - Enforces uniqueness of plate numbers and VINs
--   - Validates vehicle production year range
--   - Ensures vehicles always reference valid owners and models
--
-- Relationship Model:
--   - Many vehicles belong to one user (owner)
--   - Many vehicles belong to one vehicle model
--   - Vehicle model is a normalized reference entity
--
-- Deletion Strategy:
--   - Vehicles are never physically deleted in operational flow
--   - Soft deletion is used for historical integrity
--   - Foreign key restrictions prevent accidental orphaning
--
-- Lifecycle Strategy:
--   - Vehicles transition through defined operational states
--   - Status field controls operational availability
--
-- Performance Strategy:
--   - Indexed for owner-based queries
--   - Indexed for plate-based lookup (fast search)
--   - Indexed for status filtering (fleet operations)
-- =============================================================



-- =============================================================
-- TABLE CREATION: VEHICLES
-- =============================================================
-- Creates the primary fleet asset table representing all
-- registered vehicles in the system.
--
-- This table acts as the operational backbone for:
--   - fleet tracking
--   - vehicle assignment
--   - maintenance workflows
--   - trip logging systems
-- =============================================================
CREATE TABLE vehicles (



    -- =========================================================
    -- VEHICLE IDENTITY
    -- =========================================================
    -- Internal system identifier for each vehicle record.
    --
    -- Used for:
    --   - joins across system tables
    --   - internal tracking
    --   - API references
    -- =========================================================
                          id BIGINT NOT NULL AUTO_INCREMENT,



    -- =========================================================
    -- OWNERSHIP RELATIONSHIP
    -- =========================================================
    -- Links vehicle to the user who owns or manages it.
    --
    -- Purpose:
    --   - defines ownership structure
    --   - enables user-based fleet management
    -- =========================================================
                          owner_id BIGINT NOT NULL,



    -- =========================================================
    -- VEHICLE MODEL REFERENCE
    -- =========================================================
    -- Links to standardized vehicle model definition.
    --
    -- Purpose:
    --   - enforces normalization (3NF)
    --   - avoids storing redundant make/model data
    -- =========================================================
                          model_id BIGINT NOT NULL,



    -- =========================================================
    -- VEHICLE IDENTIFICATION DATA
    -- =========================================================
    -- Stores real-world identifiers for the vehicle.
    --
    -- Used for:
    --   - legal identification
    --   - fleet tracking
    --   - enforcement and compliance
    -- =========================================================
                          plate_number VARCHAR(20) NOT NULL,
                          year SMALLINT NOT NULL,
                          color VARCHAR(50) NULL DEFAULT NULL,
                          vin VARCHAR(50) NULL DEFAULT NULL,



    -- =========================================================
    -- OPERATIONAL STATUS LAYER
    -- =========================================================
    -- Defines the current operational state of a vehicle.
    --
    -- Used for:
    --   - availability tracking
    --   - maintenance workflows
    --   - security and theft tracking
    --
    -- Vehicles are never hard deleted; they are managed via status.
    -- =========================================================
                          status ENUM('ACTIVE','INACTIVE','STOLEN',
                'UNDER_MAINTENANCE','DECOMMISSIONED')
                NOT NULL DEFAULT 'ACTIVE',



    -- =========================================================
    -- SOFT DELETION SYSTEM
    -- =========================================================
    -- Ensures vehicles are never physically removed from database.
    --
    -- Purpose:
    --   - preserves historical records
    --   - supports audit trails
    --   - maintains referential integrity
    -- =========================================================
                          is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
                          deleted_at TIMESTAMP NULL DEFAULT NULL,



    -- =========================================================
    -- AUDIT TRAIL
    -- =========================================================
    -- Tracks creation and modification timestamps.
    --
    -- Used for:
    --   - system auditing
    --   - lifecycle tracking
    --   - operational monitoring
    -- =========================================================
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                              ON UPDATE CURRENT_TIMESTAMP,



    -- =========================================================
    -- PRIMARY KEY
    -- =========================================================
    -- Uniquely identifies each vehicle record in the system.
    -- =========================================================
                          PRIMARY KEY (id),



    -- =========================================================
    -- UNIQUENESS CONSTRAINTS
    -- =========================================================
    -- Ensures real-world vehicle identifiers are not duplicated.
    --
    -- Prevents:
    --   - duplicate plate numbers
    --   - duplicate VIN entries
    -- =========================================================
                          CONSTRAINT uq_vehicles_plate UNIQUE (plate_number),
                          CONSTRAINT uq_vehicles_vin UNIQUE (vin),



    -- =========================================================
    -- BUSINESS VALIDATION RULES
    -- =========================================================
    -- Ensures data integrity for vehicle attributes.
    --
    -- Year constraint ensures:
    --   - realistic manufacturing years
    --   - prevents invalid future/past entries
    --
    -- Plate constraint ensures:
    --   - non-empty identifiers
    -- =========================================================
                          CONSTRAINT chk_vehicles_year
                              CHECK (year >= 1980 AND year <= 2030),

    CONSTRAINT chk_vehicles_plate
        CHECK (CHAR_LENGTH(TRIM(plate_number)) > 0),



    -- =========================================================
    -- FOREIGN KEY: OWNER
    -- =========================================================
    -- Ensures vehicle belongs to a valid user.
    --
    -- ON DELETE RESTRICT:
    --   prevents deletion of users who still own vehicles
    -- =========================================================
    CONSTRAINT fk_vehicles_owner
        FOREIGN KEY (owner_id) REFERENCES users (id)
        ON UPDATE CASCADE ON DELETE RESTRICT,



    -- =========================================================
    -- FOREIGN KEY: MODEL
    -- =========================================================
    -- Links vehicle to standardized vehicle model.
    --
    -- ON DELETE RESTRICT:
    --   prevents deletion of models in use by vehicles
    -- =========================================================
    CONSTRAINT fk_vehicles_model
        FOREIGN KEY (model_id) REFERENCES vehicle_models (id)
        ON UPDATE CASCADE ON DELETE RESTRICT
);



-- =============================================================
-- INDEXING SECTION
-- =============================================================
-- Optimizes high-frequency queries on vehicle data.
--
-- Key Optimizations:
--   - fast lookup by owner (fleet listing)
--   - fast search by plate number
--   - efficient filtering by status
--   - efficient soft-deletion queries
-- =============================================================



-- =============================================================
-- OWNER LOOKUP INDEX
-- =============================================================
-- Speeds up fetching all vehicles belonging to a user.
-- =============================================================
CREATE INDEX idx_vehicles_owner_id
    ON vehicles (owner_id);



-- =============================================================
-- PLATE NUMBER INDEX
-- =============================================================
-- Optimizes vehicle search operations using plate number.
-- =============================================================
CREATE INDEX idx_vehicles_plate
    ON vehicles (plate_number);



-- =============================================================
-- STATUS FILTER INDEX
-- =============================================================
-- Improves performance for fleet state queries.
-- =============================================================
CREATE INDEX idx_vehicles_status
    ON vehicles (status);



-- =============================================================
-- SOFT DELETE INDEX
-- =============================================================
-- Optimizes filtering of active vs deleted vehicles.
-- =============================================================
CREATE INDEX idx_vehicles_is_deleted
    ON vehicles (is_deleted);
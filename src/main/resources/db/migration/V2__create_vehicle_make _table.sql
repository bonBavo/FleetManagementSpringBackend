
-- =============================================================
-- V12__create_vehicle_makes_table.sql
-- =============================================================
-- Purpose:
--   Introduces a centralized master/reference table for vehicle
--   manufacturers in the system.
--
-- Architectural Role:
--   This table is part of the Master Data layer of the system.
--   It ensures that vehicle manufacturer information is stored
--   once and referenced consistently across the entire database.
--
-- Design Principles:
--   - Applies Third Normal Form (3NF) by eliminating duplication
--   - Implements a lookup/reference pattern for standardization
--   - Ensures consistent naming of vehicle manufacturers
--
-- System Responsibilities:
--   - Maintains a controlled list of valid vehicle manufacturers
--   - Prevents duplication of manufacturer names across tables
--   - Serves as a reference source for vehicle-related entities
--
-- Data Integrity Strategy:
--   - Enforces uniqueness of manufacturer names
--   - Prevents empty or invalid manufacturer entries
--   - Ensures all makes are stored in a normalized structure
--
-- Relationship Role:
--   - Acts as a parent/reference table for vehicles and models
--   - Enables one-to-many relationships (one make → many vehicles)
--
-- Performance Strategy:
--   - Indexed for fast lookup by name
--   - Optimized for frequent joins with vehicle-related tables
--
-- Scalability Considerations:
--   - Supports large-scale fleet systems with many manufacturers
--   - Avoids redundancy as system data grows
-- =============================================================


-- =============================================================
-- TABLE CREATION: VEHICLE MAKES
-- =============================================================
-- Creates a reference table for vehicle manufacturers.
--
-- This table standardizes manufacturer data and ensures
-- consistency across all vehicle-related entities.
--
-- It acts as a foundation for vehicle classification
-- within the fleet management system.
-- =============================================================
CREATE TABLE vehicle_makes (



    -- =========================================================
    -- INTERNAL IDENTIFIER
    -- =========================================================
    -- Unique system-generated identifier for each manufacturer.
    --
    -- Used for:
    --   - relationships with vehicles table
    --   - internal joins
    --   - system-level indexing
    -- =========================================================
                               id BIGINT NOT NULL AUTO_INCREMENT,



    -- =========================================================
    -- MANUFACTURER IDENTITY
    -- =========================================================
    -- Stores the official name of the vehicle manufacturer.
    --
    -- Purpose:
    --   - acts as the primary business identifier
    --   - used in UI display and reporting
    --   - ensures standardized naming across system
    -- =========================================================
                               name VARCHAR(100) NOT NULL,



    -- =========================================================
    -- OPTIONAL METADATA
    -- =========================================================
    -- Stores additional descriptive information about the
    -- manufacturer, such as origin country.
    --
    -- Purpose:
    --   - supports analytics and reporting
    --   - enhances contextual data for fleet insights
    -- =========================================================
                               country_of_origin VARCHAR(100) NULL DEFAULT NULL,



    -- =========================================================
    -- AUDIT TRACKING
    -- =========================================================
    -- Records when the manufacturer entry was created.
    --
    -- Used for:
    --   - data auditing
    --   - system tracking
    --   - administrative history
    -- =========================================================
                               created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,



    -- =========================================================
    -- PRIMARY KEY DEFINITION
    -- =========================================================
    -- Ensures each manufacturer record is uniquely identifiable
    -- within the system.
    -- =========================================================
                               PRIMARY KEY (id),



    -- =========================================================
    -- UNIQUENESS CONSTRAINT
    -- =========================================================
    -- Ensures no duplicate manufacturer names exist.
    --
    -- Prevents:
    --   - redundant entries (e.g., multiple "Toyota")
    --   - inconsistent naming across the system
    -- =========================================================
                               CONSTRAINT uq_vehicle_makes_name UNIQUE (name),



    -- =========================================================
    -- DATA VALIDATION RULE
    -- =========================================================
    -- Ensures manufacturer names are not empty or whitespace.
    --
    -- Protects against:
    --   - invalid inserts
    --   - blank or meaningless records
    -- =========================================================
                               CONSTRAINT chk_make_name_not_blank
                                   CHECK (CHAR_LENGTH(TRIM(name)) > 0)
);



-- =============================================================
-- INDEXING SECTION
-- =============================================================
-- Optimizes search operations on manufacturer names.
--
-- Improves performance for:
--   - filtering vehicles by make
--   - lookup operations in dropdowns/UI
--   - joins with vehicles and models tables
-- =============================================================
CREATE INDEX idx_vehicle_makes_name
    ON vehicle_makes (name);
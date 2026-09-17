-- =============================================================
-- V5__create_vehicle_models_table.sql
-- =============================================================
-- Purpose:
--   Introduces a standardized reference table for vehicle models.
--
-- Architectural Role:
--   This table is part of the system's Master Data layer.
--   It sits between vehicle manufacturers (makes) and actual vehicles.
--
-- System Responsibilities:
--   - Maintains a controlled catalog of vehicle models
--   - Ensures consistency of model naming across the system
--   - Eliminates duplication of model data in vehicle records
--   - Provides a structured classification layer for fleet assets
--
-- Data Design Strategy:
--   - Implements Third Normal Form (3NF)
--   - Separates manufacturer and model concerns
--   - Supports hierarchical vehicle classification (make → model)
--
-- Relationship Model:
--   - Each model belongs to exactly one manufacturer (make)
--   - One manufacturer can have many models
--   - Each model can be referenced by many vehicles
--
-- Data Integrity Strategy:
--   - Prevents duplicate model names under the same manufacturer
--   - Enforces valid relationship to vehicle_makes table
--   - Ensures optional technical attributes remain valid
--
-- Business Logic Role:
--   - Acts as the authoritative source for vehicle model definitions
--   - Used in vehicle creation and fleet categorization
--
-- Performance Strategy:
--   - Indexed on manufacturer reference for fast lookup
--   - Optimized for dropdowns, filtering, and joins
--
-- Scalability Considerations:
--   - Supports large catalogs of vehicle models per manufacturer
--   - Easily extensible for additional vehicle specifications
-- =============================================================


-- =============================================================
-- TABLE CREATION: VEHICLE MODELS
-- =============================================================
-- Creates the intermediate classification layer between
-- vehicle manufacturers and individual vehicles.
--
-- This table ensures that models are stored once and reused
-- across all vehicle records, enforcing strict normalization.
-- =============================================================
CREATE TABLE vehicle_models (



    -- =========================================================
    -- INTERNAL IDENTIFIER
    -- =========================================================
    -- Unique system-generated identifier for each vehicle model.
    --
    -- Used for:
    --   - foreign key relationships
    --   - internal joins
    --   - system-level references
    -- =========================================================
                                id BIGINT NOT NULL AUTO_INCREMENT,



    -- =========================================================
    -- MANUFACTURER RELATIONSHIP
    -- =========================================================
    -- Links each vehicle model to its parent manufacturer.
    --
    -- Purpose:
    --   - enforces hierarchical structure (make → model)
    --   - ensures models belong to valid manufacturers
    -- =========================================================
                                make_id BIGINT NOT NULL,



    -- =========================================================
    -- MODEL IDENTITY
    -- =========================================================
    -- Stores the official name of the vehicle model.
    --
    -- Purpose:
    --   - used in UI and system display
    --   - standardizes vehicle classification
    -- =========================================================
                                name VARCHAR(100) NOT NULL,



    -- =========================================================
    -- TECHNICAL SPECIFICATIONS (OPTIONAL DATA)
    -- =========================================================
    -- Stores model-level engineering or specification data.
    --
    -- Purpose:
    --   - supports analytics and reporting
    --   - enables fuel and performance analysis
    -- =========================================================
                                fuel_capacity_l DECIMAL(6,2) NULL DEFAULT NULL,



    -- =========================================================
    -- AUDIT TRACKING
    -- =========================================================
    -- Records when the model entry was created.
    --
    -- Used for:
    --   - administrative tracking
    --   - historical data analysis
    -- =========================================================
                                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,



    -- =========================================================
    -- PRIMARY KEY DEFINITION
    -- =========================================================
    -- Ensures each vehicle model record is uniquely identifiable.
    -- =========================================================
                                PRIMARY KEY (id),



    -- =========================================================
    -- UNIQUENESS CONSTRAINT
    -- =========================================================
    -- Ensures that a model name is unique within a manufacturer.
    --
    -- Prevents:
    --   - duplicate models under same make
    --   - inconsistent model naming
    -- =========================================================
                                CONSTRAINT uq_vehicle_model_make UNIQUE (make_id, name),



    -- =========================================================
    -- DATA VALIDATION RULE
    -- =========================================================
    -- Ensures fuel capacity is logically valid if provided.
    --
    -- Prevents:
    --   - negative or zero fuel capacity values
    --   - invalid engineering data entries
    -- =========================================================
                                CONSTRAINT chk_fuel_capacity
                                    CHECK (fuel_capacity_l IS NULL OR fuel_capacity_l > 0),



    -- =========================================================
    -- FOREIGN KEY RELATIONSHIP
    -- =========================================================
    -- Links model to its parent manufacturer.
    --
    -- Behavior:
    --   ON UPDATE CASCADE:
    --       keeps relationships consistent if make ID changes
    --
    --   ON DELETE RESTRICT:
    --       prevents deletion of a manufacturer if models exist
    --
    -- Ensures:
    --   - referential integrity
    --   - no orphaned models
    -- =========================================================
                                CONSTRAINT fk_model_make
                                    FOREIGN KEY (make_id) REFERENCES vehicle_makes (id)
                                        ON UPDATE CASCADE ON DELETE RESTRICT
);



-- =============================================================
-- INDEXING SECTION
-- =============================================================
-- Optimizes lookup and join performance for vehicle models.
--
-- Primary Use Cases:
--   - retrieving models by manufacturer
--   - populating UI dropdowns
--   - joining with vehicles table
-- =============================================================
CREATE INDEX idx_vehicle_models_make_id
    ON vehicle_models (make_id);
-- =============================================================
-- V11__create_fuel_logs_table.sql
-- =============================================================
-- Purpose:
--   Stores meaningful fuel-related events generated from vehicles.
--
-- Architectural Role:
--   This table acts as a structured analytics layer for fuel behavior.
--   It does NOT store raw sensor streams (those go to MongoDB or whatever ill decides to use later).
--
--   Instead, it stores filtered, important fuel events only.
--
-- System Responsibilities:
--   - Tracks fuel level changes over time
--   - Detects refueling and fuel drop events
--   - Captures fuel state at key trip moments
--   - Provides data for fuel analytics and theft detection
--
-- IoT Data Flow Role:
--   Raw fuel sensor readings → MongoDB (high-frequency storage)
--   Significant events → MySQL (this table)
--
-- Business Role:
--   Used for:
--   - fuel theft detection
--   - refueling monitoring
--   - trip fuel analysis
--   - fleet cost optimization
--
-- Data Design Strategy:
--   - Event-based storage (not continuous streaming)
--   - Each row represents a meaningful fuel state change
--   - Combines sensor data with contextual trip/vehicle info
--
-- Integrity Strategy:
--   - Ensures valid fuel percentage (0–100%)
--   - Ensures fuel volume cannot be negative
--   - Maintains relational integrity with vehicles and trips
--
-- Performance Strategy:
--   - Indexed for vehicle-based fuel history queries
--   - Indexed for event-type filtering (refuel, theft, etc.)
--   - Indexed for time-series fuel analytics
--
-- Scalability Considerations:
--   Designed for high-frequency IoT systems where raw data
--   is offloaded to MongoDB and only meaningful events are stored
--   in a relational database for reporting and analytics.
-- =============================================================


CREATE TABLE fuel_logs (



    -- =========================================================
    -- PRIMARY IDENTIFIER
    -- =========================================================
    -- Unique ID for each fuel event record.
    -- =========================================================
                           id BIGINT NOT NULL AUTO_INCREMENT,



    -- =========================================================
    -- VEHICLE CONTEXT
    -- =========================================================
    -- vehicle_id → identifies which vehicle generated fuel event
    -- =========================================================
                           vehicle_id BIGINT NOT NULL,



    -- =========================================================
    -- TRIP CONTEXT (OPTIONAL)
    -- =========================================================
    -- trip_id → links fuel event to a specific journey
    --
    -- NULL means:
    --   event occurred outside a defined trip
    -- =========================================================
                           trip_id BIGINT NULL DEFAULT NULL,



    -- =========================================================
    -- FUEL MEASUREMENT DATA
    -- =========================================================
    -- fuel_level_pct → fuel percentage remaining (0–100%)
    -- fuel_volume_l  → estimated fuel volume in liters
    -- =========================================================
                           fuel_level_pct DECIMAL(5,2) NOT NULL,
                           fuel_volume_l DECIMAL(8,3) NULL DEFAULT NULL,



    -- =========================================================
    -- EVENT CLASSIFICATION
    -- =========================================================
    -- Defines type of fuel event:
    --
    -- TRIP_START_SNAPSHOT → fuel at trip start
    -- TRIP_END_SNAPSHOT   → fuel at trip end
    -- REFUEL_DETECTED     → fuel increase detected
    -- FUEL_DROP_DETECTED  → sudden fuel decrease (possible theft)
    -- MANUAL_READING      → manually recorded fuel value
    -- PERIODIC_SNAPSHOT   → scheduled sensor reading
    -- =========================================================
                           event_type ENUM(
        'TRIP_START_SNAPSHOT','TRIP_END_SNAPSHOT',
        'REFUEL_DETECTED','FUEL_DROP_DETECTED',
        'MANUAL_READING','PERIODIC_SNAPSHOT'
    ) NOT NULL DEFAULT 'PERIODIC_SNAPSHOT',



    -- =========================================================
    -- LOCATION DATA
    -- =========================================================
    -- latitude  → where fuel event occurred
    -- longitude → geographic position
    -- =========================================================
                           latitude DECIMAL(10,8) NULL DEFAULT NULL,
                           longitude DECIMAL(11,8) NULL DEFAULT NULL,



    -- =========================================================
    -- TIMESTAMP DATA
    -- =========================================================
    -- recorded_at → time sensor generated reading
    -- created_at  → time record stored in database
    -- =========================================================
                           recorded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,



    -- =========================================================
    -- PRIMARY KEY
    -- =========================================================
                           PRIMARY KEY (id),



    -- =========================================================
    -- DATA VALIDATION CONSTRAINTS
    -- =========================================================

                           CONSTRAINT chk_fuel_level
                               CHECK (fuel_level_pct BETWEEN 0 AND 100),

                           CONSTRAINT chk_fuel_volume
                               CHECK (fuel_volume_l IS NULL OR fuel_volume_l >= 0),



    -- =========================================================
    -- FOREIGN KEY RELATIONSHIPS
    -- =========================================================

                           CONSTRAINT fk_fuel_vehicle
                               FOREIGN KEY (vehicle_id) REFERENCES vehicles (id)
                                   ON UPDATE CASCADE ON DELETE RESTRICT,

                           CONSTRAINT fk_fuel_trip
                               FOREIGN KEY (trip_id) REFERENCES trips (id)
                                   ON UPDATE CASCADE ON DELETE SET NULL
);



-- =============================================================
-- INDEXING SECTION
-- =============================================================
-- Optimized for fuel analytics and fleet monitoring queries.
-- =============================================================

CREATE INDEX idx_fuel_vehicle_id
    ON fuel_logs (vehicle_id);

CREATE INDEX idx_fuel_event_type
    ON fuel_logs (event_type);

CREATE INDEX idx_fuel_vehicle_time
    ON fuel_logs (vehicle_id, recorded_at);
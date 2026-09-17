-- =============================================================
-- V6__create_trips_table.sql
-- =============================================================
-- Purpose:
--   Defines the trip/journey tracking layer of the fleet system.
--
-- Architectural Role:
--   This table represents completed or ongoing vehicle journeys.
--   It is the core analytics and reporting foundation of the system.
--
-- System Responsibilities:
--   - Records every vehicle movement session (trip lifecycle)
--   - Links vehicle, driver, and IoT device per journey
--   - Stores geospatial start/end points of trips
--   - Captures performance metrics (speed, distance, fuel usage)
--   - Enables fleet analytics, reporting, and driver evaluation
--
-- Data Flow Context:
--   Trips are not manually created.
--   They are generated from real-time telemetry data (e.g., MQTT GPS streams)
--   and processed by backend services into structured journey records.
--
-- IoT Integration Role:
--   This table is a downstream consumer of device telemetry data.
--   Devices send GPS and sensor data → backend aggregates → trips stored here.
--
-- Business Role:
--   Provides structured history of vehicle usage for:
--   - fleet monitoring
--   - driver behavior analysis
--   - fuel efficiency tracking
--   - route optimization
--
-- Data Design Strategy:
--   - Each trip represents a single vehicle journey session
--   - Supports both active and completed trips
--   - Captures both raw location data and derived analytics
--   - Separates identity (vehicle/driver/device) from movement data
--
-- Integrity Strategy:
--   - Ensures trips cannot end before they start
--   - Prevents invalid speed, distance, and fuel values
--   - Maintains referential integrity with vehicles, users, devices
--
-- Performance Strategy:
--   - Indexed for vehicle-based queries (primary access pattern)
--   - Optimized for time-range filtering and reporting
--   - Supports high-volume analytical queries
--
-- Lifecycle Model:
--   ACTIVE → trip in progress
--   COMPLETED → successfully finished trip
--   CANCELLED → invalidated trip
--   INTERRUPTED → unexpectedly stopped trip
--
-- Scalability Considerations:
--   Designed for large-scale fleet systems with:
--   - continuous GPS ingestion
--   - high-frequency trip generation
--   - analytical workloads (dashboards, reports)
-- =============================================================


CREATE TABLE trips (



    -- =========================================================
    -- PRIMARY IDENTIFIER
    -- =========================================================
    -- Unique identifier for each trip record.
    -- Used internally for joins, APIs, and analytics.
    -- =========================================================
                       id BIGINT NOT NULL AUTO_INCREMENT,



    -- =========================================================
    -- CORE RELATIONSHIPS
    -- =========================================================
    -- Links trip to system entities:
    --
    -- vehicle_id → identifies which vehicle made the trip
    -- driver_id  → identifies who operated the vehicle (optional)
    -- device_id  → identifies IoT tracking device used
    --
    -- These three entities form the core trip identity.
    -- =========================================================
                       vehicle_id BIGINT NOT NULL,
                       driver_id BIGINT NULL DEFAULT NULL,
                       device_id BIGINT NOT NULL,



    -- =========================================================
    -- TRIP TIME WINDOW
    -- =========================================================
    -- Defines lifecycle of a journey:
    --
    -- start_time → when movement begins
    -- end_time   → when movement ends (nullable for active trips)
    -- =========================================================
                       start_time TIMESTAMP NOT NULL,
                       end_time TIMESTAMP NULL DEFAULT NULL,



    -- =========================================================
    -- START LOCATION DATA
    -- =========================================================
    -- GPS coordinates and human-readable location at trip start.
    -- =========================================================
                       start_latitude DECIMAL(10,8) NULL DEFAULT NULL,
                       start_longitude DECIMAL(11,8) NULL DEFAULT NULL,
                       start_address VARCHAR(500) NULL DEFAULT NULL,



    -- =========================================================
    -- END LOCATION DATA
    -- =========================================================
    -- GPS coordinates and human-readable location at trip end.
    -- =========================================================
                       end_latitude DECIMAL(10,8) NULL DEFAULT NULL,
                       end_longitude DECIMAL(11,8) NULL DEFAULT NULL,
                       end_address VARCHAR(500) NULL DEFAULT NULL,



    -- =========================================================
    -- TRIP METRICS (DERIVED DATA)
    -- =========================================================
    -- These values are computed from GPS and sensor data:
    --
    -- distance_km      → total distance traveled
    -- duration_minutes  → total trip time
    -- max_speed_kmh     → highest speed recorded
    -- avg_speed_kmh     → average speed during trip
    -- fuel_consumed_l   → estimated fuel usage
    -- =========================================================
                       distance_km DECIMAL(10,3) NULL DEFAULT NULL,
                       duration_minutes INT NULL DEFAULT NULL,
                       max_speed_kmh DECIMAL(6,2) NULL DEFAULT NULL,
                       avg_speed_kmh DECIMAL(6,2) NULL DEFAULT NULL,
                       fuel_consumed_l DECIMAL(8,3) NULL DEFAULT NULL,



    -- =========================================================
    -- TRIP STATUS STATE MACHINE
    -- =========================================================
    -- Defines current state of the trip lifecycle:
    --
    -- ACTIVE       → trip is ongoing
    -- COMPLETED    → trip finished normally
    -- CANCELLED    → trip invalidated
    -- INTERRUPTED  → trip ended unexpectedly
    -- =========================================================
                       status ENUM('ACTIVE','COMPLETED','CANCELLED','INTERRUPTED')
        NOT NULL DEFAULT 'ACTIVE',



    -- =========================================================
    -- AUDIT TRAIL
    -- =========================================================
    -- Tracks record creation and modification timestamps.
    -- =========================================================
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                           ON UPDATE CURRENT_TIMESTAMP,



    -- =========================================================
    -- PRIMARY KEY
    -- =========================================================
    -- Ensures unique identification of each trip record.
    -- =========================================================
                       PRIMARY KEY (id),



    -- =========================================================
    -- DATA VALIDATION CONSTRAINTS
    -- =========================================================
    -- Enforces logical correctness of trip analytics data.
    -- =========================================================

                       CONSTRAINT chk_trip_end_after_start
                           CHECK (end_time IS NULL OR end_time > start_time),

                       CONSTRAINT chk_trip_distance
                           CHECK (distance_km IS NULL OR distance_km >= 0),

                       CONSTRAINT chk_trip_max_speed
                           CHECK (max_speed_kmh IS NULL OR (max_speed_kmh >= 0 AND max_speed_kmh <= 300)),

                       CONSTRAINT chk_trip_avg_speed
                           CHECK (avg_speed_kmh IS NULL OR (avg_speed_kmh >= 0 AND avg_speed_kmh <= 300)),

                       CONSTRAINT chk_trip_fuel
                           CHECK (fuel_consumed_l IS NULL OR fuel_consumed_l >= 0),



    -- =========================================================
    -- FOREIGN KEY RELATIONSHIPS
    -- =========================================================
    -- Maintains referential integrity with core system entities.
    -- =========================================================

                       CONSTRAINT fk_trips_vehicle
                           FOREIGN KEY (vehicle_id) REFERENCES vehicles (id)
                               ON UPDATE CASCADE ON DELETE RESTRICT,

                       CONSTRAINT fk_trips_driver
                           FOREIGN KEY (driver_id) REFERENCES users (id)
                               ON UPDATE CASCADE ON DELETE SET NULL,

                       CONSTRAINT fk_trips_device
                           FOREIGN KEY (device_id) REFERENCES device_registry (id)
                               ON UPDATE CASCADE ON DELETE RESTRICT
);



-- =============================================================
-- INDEXING SECTION
-- =============================================================
-- Optimizes high-frequency query patterns for analytics and
-- real-time fleet monitoring systems.
-- =============================================================

CREATE INDEX idx_trips_vehicle_id     ON trips (vehicle_id);
CREATE INDEX idx_trips_driver_id      ON trips (driver_id);
CREATE INDEX idx_trips_status         ON trips (status);
CREATE INDEX idx_trips_start_time     ON trips (start_time);

CREATE INDEX idx_trips_vehicle_status ON trips (vehicle_id, status);

CREATE INDEX idx_trips_vehicle_time   ON trips (vehicle_id, start_time, end_time);
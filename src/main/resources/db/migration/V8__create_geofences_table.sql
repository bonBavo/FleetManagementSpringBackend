-- =============================================================
-- V8__create_geofences_table.sql
-- =============================================================
-- Purpose:
--   Defines spatial boundary zones (geofences) for vehicles and users.
--
-- Architectural Role:
--   This table enables location-based rules and event triggering
--   in the fleet management system.
--
--   It is a core part of the real-time monitoring and alerting layer.
--
-- System Responsibilities:
--   - Defines circular geographic zones (geofences)
--   - Triggers alerts when vehicles enter or exit zones
--   - Supports fleet monitoring and security enforcement
--   - Enables route compliance and restricted area detection
--
-- IoT / Real-time Role:
--   Geofences are evaluated against live GPS streams from devices
--   (via MQTT → backend → trip/location processing).
--
--   When a vehicle's coordinates match a geofence condition,
--   alerts/events are generated in real time.
--
-- Business Role:
--   Used for:
--   - security zones (restricted areas)
--   - delivery boundaries
--   - operational zones
--   - fleet compliance monitoring
--
-- Spatial Model:
--   Uses circular geofences defined by:
--   - center point (latitude, longitude)
--   - radius in meters
--
-- Data Design Strategy:
--   - Each geofence belongs to a user (owner)
--   - Optionally linked to a specific vehicle
--   - Supports both global and vehicle-specific geofencing
--
-- Event Strategy:
--   - alert_on_entry → trigger when vehicle enters zone
--   - alert_on_exit  → trigger when vehicle leaves zone
--
-- Integrity Strategy:
--   - Ensures valid geographic coordinates
--   - Enforces realistic radius constraints
--   - Maintains referential integrity with users and vehicles
--
-- Lifecycle Model:
--   ACTIVE   → geofence is actively monitored
--   INACTIVE → geofence ignored in processing
--
-- Performance Strategy:
--   - Indexed by owner and vehicle for fast lookup
--   - Indexed by active status for filtering active geofences
--
-- Scalability Considerations:
--   Designed for real-time evaluation against streaming GPS data
--   in large-scale fleet tracking systems.
-- =============================================================


CREATE TABLE geofences (



    -- =========================================================
    -- PRIMARY IDENTIFIER
    -- =========================================================
    -- Unique ID for each geofence definition.
    -- =========================================================
                           id BIGINT NOT NULL AUTO_INCREMENT,



    -- =========================================================
    -- OWNERSHIP RELATIONSHIP
    -- =========================================================
    -- owner_id → user who created and manages the geofence
    --
    -- Used for:
    --   - access control
    --   - geofence management per user
    -- =========================================================
                           owner_id BIGINT NOT NULL,



    -- =========================================================
    -- OPTIONAL VEHICLE LINK
    -- =========================================================
    -- vehicle_id → restricts geofence to a specific vehicle
    --
    -- NULL means:
    --   geofence applies to all vehicles under owner/system scope
    -- =========================================================
                           vehicle_id BIGINT NULL DEFAULT NULL,



    -- =========================================================
    -- GEOGRAPHIC IDENTIFIER
    -- =========================================================
    -- name → human-readable geofence label
    -- description → optional metadata for context
    -- =========================================================
                           name VARCHAR(255) NOT NULL,
                           description VARCHAR(1000) NULL DEFAULT NULL,



    -- =========================================================
    -- SPATIAL CENTER POINT
    -- =========================================================
    -- Defines the center of circular geofence:
    --
    -- center_latitude  → latitude coordinate
    -- center_longitude → longitude coordinate
    -- =========================================================
                           center_latitude DECIMAL(10,8) NOT NULL,
                           center_longitude DECIMAL(11,8) NOT NULL,



    -- =========================================================
    -- GEOGRAPHIC BOUNDARY SIZE
    -- =========================================================
    -- radius_meters → size of geofence circle
    --
    -- Used to determine boundary sensitivity
    -- =========================================================
                           radius_meters INT NOT NULL,



    -- =========================================================
    -- EVENT TRIGGER SETTINGS
    -- =========================================================
    -- alert_on_entry → trigger when vehicle enters zone
    -- alert_on_exit  → trigger when vehicle leaves zone
    -- =========================================================
                           alert_on_entry BOOLEAN NOT NULL DEFAULT TRUE,
                           alert_on_exit BOOLEAN NOT NULL DEFAULT TRUE,



    -- =========================================================
    -- ACTIVITY STATE
    -- =========================================================
    -- is_active → determines whether geofence is enforced
    -- =========================================================
                           is_active BOOLEAN NOT NULL DEFAULT TRUE,



    -- =========================================================
    -- AUDIT TRAIL
    -- =========================================================
    -- Tracks creation and update timestamps
    -- =========================================================
                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                               ON UPDATE CURRENT_TIMESTAMP,



    -- =========================================================
    -- PRIMARY KEY
    -- =========================================================
                           PRIMARY KEY (id),



    -- =========================================================
    -- GEOGRAPHIC VALIDATION CONSTRAINTS
    -- =========================================================

                           CONSTRAINT chk_geofence_radius
                               CHECK (radius_meters BETWEEN 50 AND 50000),

                           CONSTRAINT chk_geofence_latitude
                               CHECK (center_latitude BETWEEN -90 AND 90),

                           CONSTRAINT chk_geofence_longitude
                               CHECK (center_longitude BETWEEN -180 AND 180),



    -- =========================================================
    -- FOREIGN KEY RELATIONSHIPS
    -- =========================================================

                           CONSTRAINT fk_geofence_owner
                               FOREIGN KEY (owner_id) REFERENCES users (id)
                                   ON UPDATE CASCADE ON DELETE CASCADE,

                           CONSTRAINT fk_geofence_vehicle
                               FOREIGN KEY (vehicle_id) REFERENCES vehicles (id)
                                   ON UPDATE CASCADE ON DELETE CASCADE
);



-- =============================================================
-- INDEXING SECTION
-- =============================================================
-- Optimizes geofence lookup and real-time evaluation.
-- =============================================================

CREATE INDEX idx_geofences_owner_id   ON geofences (owner_id);
CREATE INDEX idx_geofences_vehicle_id ON geofences (vehicle_id);
CREATE INDEX idx_geofences_is_active  ON geofences (is_active);
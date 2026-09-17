-- =============================================================
-- V17__alter_trips_add_matatu_fields.sql
-- Add matatu-specific trip data
-- =============================================================
ALTER TABLE trips
    ADD COLUMN route_code          VARCHAR(50)     NULL DEFAULT NULL AFTER status,
    ADD COLUMN passenger_count     SMALLINT        NULL DEFAULT NULL AFTER route_code,
    ADD COLUMN fare_collected_kes  DECIMAL(10,2)   NULL DEFAULT NULL AFTER passenger_count,
    ADD COLUMN trip_type           ENUM('PRIVATE','MATATU_ROUTE','DELIVERY','OTHER')
                                   NOT NULL DEFAULT 'PRIVATE' AFTER fare_collected_kes;

CREATE INDEX idx_trips_route_code ON trips (route_code);
CREATE INDEX idx_trips_trip_type  ON trips (trip_type);
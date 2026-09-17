-- =============================================================
-- V16__alter_vehicles_add_powertrain.sql
-- Add powertrain type and vehicle category to vehicles table
-- =============================================================
ALTER TABLE vehicles
    ADD COLUMN powertrain_type  ENUM('ICE','ELECTRIC','HYBRID')
                                NOT NULL DEFAULT 'ICE'
                                AFTER color,
    ADD COLUMN vehicle_category ENUM(
                                    'PERSONAL_CAR',
                                    'MATATU_14',       -- 14-seater
                                    'MATATU_33',       -- 33-seater
                                    'BUS',
                                    'TRUCK',
                                    'MOTORCYCLE',
                                    'PICKUP'
                                ) NOT NULL DEFAULT 'PERSONAL_CAR'
                                AFTER powertrain_type,
    ADD COLUMN seating_capacity TINYINT NULL DEFAULT NULL AFTER vehicle_category;

CREATE INDEX idx_vehicles_powertrain ON vehicles (powertrain_type);
CREATE INDEX idx_vehicles_category   ON vehicles (vehicle_category);

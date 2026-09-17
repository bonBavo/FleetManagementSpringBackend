-- =============================================================
-- V18__alter_fuel_logs_to_energy_logs.sql
-- Rename table and extend for EV charge monitoring
-- Same table — energy_type column drives ICE vs EV logic
-- =============================================================
ALTER TABLE fuel_logs
    RENAME TO energy_logs;

ALTER TABLE energy_logs
    ADD COLUMN energy_type  ENUM('FUEL','ELECTRIC_CHARGE')
                            NOT NULL DEFAULT 'FUEL'
                            AFTER event_type,
    ADD COLUMN charge_kwh   DECIMAL(8,3) NULL DEFAULT NULL
                            AFTER energy_type,
    ADD COLUMN charger_type ENUM('AC_SLOW','DC_FAST','SOLAR') NULL DEFAULT NULL
                            AFTER charge_kwh,
    MODIFY COLUMN fuel_level_pct DECIMAL(5,2) NULL DEFAULT NULL,
    MODIFY COLUMN fuel_volume_l  DECIMAL(8,3) NULL DEFAULT NULL;

-- Add EV-specific events to event_type
ALTER TABLE energy_logs
    MODIFY COLUMN event_type ENUM(
    'TRIP_START_SNAPSHOT',
    'TRIP_END_SNAPSHOT',
    'REFUEL_DETECTED',
    'FUEL_DROP_DETECTED',
    'CHARGE_STARTED',
    'CHARGE_COMPLETED',
    'CHARGE_INTERRUPTED',
    'LOW_CHARGE_WARNING',
    'MANUAL_READING',
    'PERIODIC_SNAPSHOT'
    ) NOT NULL DEFAULT 'PERIODIC_SNAPSHOT';

CREATE INDEX idx_energy_type ON energy_logs (energy_type);
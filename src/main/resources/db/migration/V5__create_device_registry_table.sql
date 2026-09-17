-- =============================================================
-- V5__create_device_registry_table.sql
-- NOTE: One-active-device-per-vehicle enforced in service layer
-- MySQL blocks FK on columns used in generated column expressions
-- =============================================================

CREATE TABLE device_registry (
                                 id                  BIGINT          NOT NULL AUTO_INCREMENT,
                                 vehicle_id          BIGINT          NULL DEFAULT NULL,
                                 device_serial       VARCHAR(100)    NOT NULL,
                                 sim_iccid           VARCHAR(30)     NULL DEFAULT NULL,
                                 sim_phone_number    VARCHAR(20)     NULL DEFAULT NULL,
                                 device_token        VARCHAR(255)    NOT NULL,
                                 token_expires_at    TIMESTAMP       NULL DEFAULT NULL,
                                 firmware_version    VARCHAR(50)     NULL DEFAULT NULL,
                                 last_seen_at        TIMESTAMP       NULL DEFAULT NULL,
                                 status              ENUM('ACTIVE','INACTIVE','DECOMMISSIONED','UNASSIGNED')
                                        NOT NULL DEFAULT 'UNASSIGNED',
                                 assigned_at         TIMESTAMP       NULL DEFAULT NULL,
                                 deactivated_at      TIMESTAMP       NULL DEFAULT NULL,
                                 created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 updated_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
                                     ON UPDATE CURRENT_TIMESTAMP,

                                 PRIMARY KEY (id),

                                 CONSTRAINT uq_device_serial UNIQUE (device_serial),
                                 CONSTRAINT uq_device_token  UNIQUE (device_token),
                                 CONSTRAINT uq_sim_iccid     UNIQUE (sim_iccid),

                                 CONSTRAINT fk_device_vehicle
                                     FOREIGN KEY (vehicle_id)
                                         REFERENCES vehicles (id)
                                         ON UPDATE CASCADE
                                         ON DELETE RESTRICT
);

CREATE INDEX idx_device_vehicle_id ON device_registry (vehicle_id);
CREATE INDEX idx_device_status     ON device_registry (status);
CREATE INDEX idx_device_last_seen  ON device_registry (last_seen_at);

-- Composite index to enforce one active device per vehicle at query level
-- Application service checks this before activating any device
CREATE INDEX idx_device_vehicle_status ON device_registry (vehicle_id, status);
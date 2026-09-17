-- =============================================================
-- V15__create_sacco_memberships_table.sql
-- Vehicle belongs to SACCO — M:M with role (owner, member, leased)
-- One vehicle can only have ONE active SACCO membership at a time
-- =============================================================
CREATE TABLE sacco_memberships (
                                   id          BIGINT      NOT NULL AUTO_INCREMENT,
                                   sacco_id    BIGINT      NOT NULL,
                                   vehicle_id  BIGINT      NOT NULL,
                                   owner_id    BIGINT      NOT NULL,   -- user who registered in sacco
                                   role        ENUM('OWNER','MEMBER','LEASED') NOT NULL DEFAULT 'MEMBER',
                                   route_code  VARCHAR(50) NULL DEFAULT NULL,   -- e.g. 'CBD-WESTLANDS-44'
                                   joined_at   TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   left_at     TIMESTAMP   NULL DEFAULT NULL,
                                   is_active   BOOLEAN     NOT NULL DEFAULT TRUE,
                                   created_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   updated_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
                                       ON UPDATE CURRENT_TIMESTAMP,

                                   PRIMARY KEY (id),

    -- One active membership per vehicle at a time
                                   CONSTRAINT uq_active_vehicle_sacco
                                       UNIQUE (vehicle_id, is_active),

                                   CONSTRAINT fk_membership_sacco
                                       FOREIGN KEY (sacco_id)  REFERENCES saccos (id)
                                           ON UPDATE CASCADE ON DELETE RESTRICT,
                                   CONSTRAINT fk_membership_vehicle
                                       FOREIGN KEY (vehicle_id) REFERENCES vehicles (id)
                                           ON UPDATE CASCADE ON DELETE RESTRICT,
                                   CONSTRAINT fk_membership_owner
                                       FOREIGN KEY (owner_id) REFERENCES users (id)
                                           ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE INDEX idx_membership_sacco_id   ON sacco_memberships (sacco_id);
CREATE INDEX idx_membership_vehicle_id ON sacco_memberships (vehicle_id);
CREATE INDEX idx_membership_is_active  ON sacco_memberships (is_active);

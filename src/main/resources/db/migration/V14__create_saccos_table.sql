-- =============================================================
-- V14__create_saccos_table.sql
-- Kenyan transport co-operatives — extensible to any fleet group
-- =============================================================
CREATE TABLE saccos (
                        id                  BIGINT          NOT NULL AUTO_INCREMENT,
                        name                VARCHAR(255)    NOT NULL,
                        registration_number VARCHAR(100)    NOT NULL,   -- official govt reg
                        route_description   VARCHAR(500)    NULL DEFAULT NULL,
                        contact_email       VARCHAR(255)    NULL DEFAULT NULL,
                        contact_phone       VARCHAR(20)     NULL DEFAULT NULL,
                        county              VARCHAR(100)    NULL DEFAULT NULL,
                        is_active           BOOLEAN         NOT NULL DEFAULT TRUE,
                        created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
                            ON UPDATE CURRENT_TIMESTAMP,
                        PRIMARY KEY (id),
                        CONSTRAINT uq_sacco_reg_number UNIQUE (registration_number),
                        CONSTRAINT chk_sacco_name      CHECK (CHAR_LENGTH(TRIM(name)) > 0)
);

CREATE INDEX idx_saccos_is_active ON saccos (is_active);

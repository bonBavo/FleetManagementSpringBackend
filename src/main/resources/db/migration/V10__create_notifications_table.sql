CREATE TABLE notifications (

                               id BIGINT NOT NULL AUTO_INCREMENT,
                               user_id BIGINT NOT NULL,
                               alert_id BIGINT NULL DEFAULT NULL,

                               title VARCHAR(255) NOT NULL,
                               body TEXT NOT NULL,

                               channel ENUM('PUSH','SMS','EMAIL','IN_APP')
        NOT NULL DEFAULT 'PUSH',

                               PRIMARY KEY (id),
                               INDEX idx_notifications_user (user_id)
);
-- Migration to add missing columns to notifications table based on Notification entity
ALTER TABLE notifications
    ADD COLUMN delivery_status ENUM('PENDING', 'SENT', 'DELIVERED', 'FAILED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    ADD COLUMN failure_reason VARCHAR(500) NULL,
    ADD COLUMN sent_at TIMESTAMP NULL,
    ADD COLUMN delivered_at TIMESTAMP NULL,
    ADD COLUMN is_read BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN read_at TIMESTAMP NULL,
    ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- Add indexes for performance
CREATE INDEX idx_notif_is_read ON notifications (is_read);
CREATE INDEX idx_notif_user_unread ON notifications (user_id, is_read, created_at);

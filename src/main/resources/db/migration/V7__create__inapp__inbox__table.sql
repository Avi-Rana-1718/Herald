CREATE TABLE in_app_notifications (
    id VARCHAR(255) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    notification_id VARCHAR(255) REFERENCES notifications (notification_id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_in_app_notifications_notification_id ON in_app_notifications (notification_id);

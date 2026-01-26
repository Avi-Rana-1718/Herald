CREATE TYPE notificationType AS ENUM ('EMAIL', 'SMS', 'WHATSAPP');
CREATE TYPE notificationStatus AS ENUM('DELIVERED', 'FAILED', 'REQUESTED', 'UNKNOWN');

CREATE TABLE notifications (
    notification_id UUID PRIMARY KEY,
    reference_id VARCHAR(200) UNIQUE,
    user_id UUID NOT NULL,
    trigger_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    type notificationType NOT NULL,
    status notificationStatus NOT NULL,
    retry_count INTEGER DEFAULT 0
);
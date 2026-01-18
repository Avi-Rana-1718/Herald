CREATE TYPE notificationType AS ENUM ('email', 'sms', 'whatsapp');
CREATE TYPE notificationStatus AS ENUM('delivered', 'failed', 'requested', 'unknown');

CREATE TABLE notification (
    notificationId UUID PRIMARY KEY,
    userId UUID NOT NULL,
    triggerDate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    type notificationType NOT NULL,
    status notificationStatus NOT NULL,
    retryCount INTEGER DEFAULT 0
);
CREATE TYPE notification_type AS ENUM ('EMAIL', 'SMS', 'WHATSAPP');
CREATE TYPE notification_status AS ENUM('QUEUED', 'FAILED', 'REQUESTED');

CREATE TABLE notifications (
    notification_id UUID PRIMARY KEY,
    reference_id VARCHAR(200) UNIQUE,
    user_id UUID NOT NULL,
    trigger_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    type notification_type NOT NULL,
    status notification_status NOT NULL DEFAULT 'QUEUED',
    retry_count INTEGER DEFAULT 0
);
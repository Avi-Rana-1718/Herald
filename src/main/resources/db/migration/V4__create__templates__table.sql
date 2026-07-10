CREATE TABLE templates (
    template_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    template_name VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    lang_code VARCHAR(10) NOT NULL,
    type notification_type NOT NULL,
    variables JSONB,
    metadata JSONB,
    version INTEGER NOT NULL DEFAULT 1,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_template UNIQUE (template_name, type, lang_code)
);

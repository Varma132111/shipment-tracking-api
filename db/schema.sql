CREATE TABLE IF NOT EXISTS shipments (
    id BIGSERIAL PRIMARY KEY,
    company_id VARCHAR(64) NOT NULL,
    shipment_id VARCHAR(64) NOT NULL,
    origin_address VARCHAR(255),
    destination_address VARCHAR(255),
    carrier VARCHAR(100),
    current_status VARCHAR(32) NOT NULL DEFAULT 'PICKUP',
    latest_location JSONB,
    eta TIMESTAMPTZ,
    condition VARCHAR(32),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_shipments_company_shipment UNIQUE (company_id, shipment_id)
);

CREATE TABLE IF NOT EXISTS shipment_events (
    id BIGSERIAL NOT NULL,
    event_id UUID NOT NULL,
    company_id VARCHAR(64) NOT NULL,
    shipment_id VARCHAR(64) NOT NULL,
    event_type VARCHAR(32) NOT NULL,
    event_timestamp TIMESTAMPTZ NOT NULL,
    location JSONB,
    metadata JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (id),
    CONSTRAINT uk_shipment_events_event_id UNIQUE (event_id)
);

CREATE TABLE IF NOT EXISTS webhooks (
    id BIGSERIAL PRIMARY KEY,
    webhook_id UUID NOT NULL,
    company_id VARCHAR(64) NOT NULL,
    target_url VARCHAR(1024) NOT NULL,
    secret VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_webhooks_webhook_id UNIQUE (webhook_id)
);

CREATE TABLE IF NOT EXISTS webhook_delivery_logs (
    id BIGSERIAL PRIMARY KEY,
    webhook_id UUID NOT NULL,
    company_id VARCHAR(64) NOT NULL,
    shipment_id VARCHAR(64) NOT NULL,
    event_id UUID NOT NULL,
    status VARCHAR(32) NOT NULL,
    response_code INTEGER,
    response_body TEXT,
    attempt_number INTEGER NOT NULL DEFAULT 1,
    delivered_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS api_rate_limits (
    id BIGSERIAL PRIMARY KEY,
    company_id VARCHAR(64) NOT NULL,
    window_start TIMESTAMPTZ NOT NULL,
    request_count INTEGER NOT NULL DEFAULT 0,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_rate_limits_company_window UNIQUE (company_id, window_start)
);

CREATE INDEX IF NOT EXISTS idx_events_company_shipment_time
    ON shipment_events (company_id, shipment_id, event_timestamp DESC);

CREATE INDEX IF NOT EXISTS idx_events_company_shipment_id
    ON shipment_events (company_id, shipment_id, id);

CREATE INDEX IF NOT EXISTS idx_events_location_gin
    ON shipment_events USING GIN (location);

CREATE INDEX IF NOT EXISTS idx_webhooks_company_active
    ON webhooks (company_id, active);

CREATE INDEX IF NOT EXISTS idx_delivery_logs_company_event
    ON webhook_delivery_logs (company_id, event_id);

CREATE INDEX IF NOT EXISTS idx_rate_limits_company_window
    ON api_rate_limits (company_id, window_start);

-- Keep events append-only by application behavior (no update/delete paths).

package com.logistics.shipmenttracking.dto;

import java.util.UUID;

public class WebhookResponse {
    private UUID webhookId;
    private String targetUrl;
    private boolean active;

    public WebhookResponse(UUID webhookId, String targetUrl, boolean active) {
        this.webhookId = webhookId;
        this.targetUrl = targetUrl;
        this.active = active;
    }

    public UUID getWebhookId() {
        return webhookId;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public boolean isActive() {
        return active;
    }
}

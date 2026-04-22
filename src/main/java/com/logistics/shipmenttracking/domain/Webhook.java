package com.logistics.shipmenttracking.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "webhooks")
public class Webhook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "webhook_id", nullable = false, unique = true)
    private UUID webhookId;

    @Column(name = "company_id", nullable = false)
    private String companyId;

    @Column(name = "target_url", nullable = false)
    private String targetUrl;

    @Column(name = "secret")
    private String secret;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public Webhook() {
    }

    public Webhook(UUID webhookId, String companyId, String targetUrl, String secret) {
        this.webhookId = webhookId;
        this.companyId = companyId;
        this.targetUrl = targetUrl;
        this.secret = secret;
        this.active = true;
    }

    public Long getId() {
        return id;
    }

    public UUID getWebhookId() {
        return webhookId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public String getSecret() {
        return secret;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

package com.logistics.shipmenttracking.domain;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "webhook_delivery_logs")
public class WebhookDeliveryLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "webhook_id", nullable = false)
    private UUID webhookId;

    @Column(name = "company_id", nullable = false)
    private String companyId;

    @Column(name = "shipment_id", nullable = false)
    private String shipmentId;

    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "response_code")
    private Integer responseCode;

    @Column(name = "response_body")
    private String responseBody;

    @Column(name = "attempt_number", nullable = false)
    private Integer attemptNumber = 1;

    @Column(name = "delivered_at")
    private OffsetDateTime deliveredAt;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public static WebhookDeliveryLog success(Webhook webhook, ShipmentEvent event, Integer responseCode, String responseBody) {
        WebhookDeliveryLog log = new WebhookDeliveryLog();
        log.webhookId = webhook.getWebhookId();
        log.companyId = webhook.getCompanyId();
        log.shipmentId = event.getShipmentId();
        log.eventId = event.getEventId();
        log.status = "SUCCESS";
        log.responseCode = responseCode;
        log.responseBody = responseBody;
        log.deliveredAt = OffsetDateTime.now();
        return log;
    }

    public static WebhookDeliveryLog failed(Webhook webhook, ShipmentEvent event, String message) {
        WebhookDeliveryLog log = new WebhookDeliveryLog();
        log.webhookId = webhook.getWebhookId();
        log.companyId = webhook.getCompanyId();
        log.shipmentId = event.getShipmentId();
        log.eventId = event.getEventId();
        log.status = "FAILED";
        log.responseBody = message;
        return log;
    }
}

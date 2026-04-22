package com.logistics.shipmenttracking.service;

import com.logistics.shipmenttracking.domain.ShipmentEvent;
import com.logistics.shipmenttracking.domain.Webhook;
import com.logistics.shipmenttracking.domain.WebhookDeliveryLog;
import com.logistics.shipmenttracking.dto.CreateWebhookRequest;
import com.logistics.shipmenttracking.dto.WebhookResponse;
import com.logistics.shipmenttracking.exception.ApiException;
import com.logistics.shipmenttracking.repository.WebhookDeliveryLogRepository;
import com.logistics.shipmenttracking.repository.WebhookRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class WebhookService {

    private final WebhookRepository webhookRepository;
    private final WebhookDeliveryLogRepository webhookDeliveryLogRepository;
    private final RestTemplate restTemplate;

    public WebhookService(WebhookRepository webhookRepository,
                          WebhookDeliveryLogRepository webhookDeliveryLogRepository,
                          RestTemplate restTemplate) {
        this.webhookRepository = webhookRepository;
        this.webhookDeliveryLogRepository = webhookDeliveryLogRepository;
        this.restTemplate = restTemplate;
    }

    public WebhookResponse create(String companyId, CreateWebhookRequest request) {
        Webhook webhook = new Webhook(UUID.randomUUID(), companyId, request.getTargetUrl(), request.getSecret());
        webhookRepository.save(webhook);
        return new WebhookResponse(webhook.getWebhookId(), webhook.getTargetUrl(), webhook.isActive());
    }

    public void delete(String companyId, UUID webhookId) {
        Webhook webhook = webhookRepository.findByWebhookIdAndCompanyId(webhookId, companyId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "WEBHOOK_NOT_FOUND", "Webhook not found"));
        webhook.setActive(false);
        webhookRepository.save(webhook);
    }

    @Async
    public void notifyStatusChange(String companyId, ShipmentEvent event) {
        List<Webhook> webhooks = webhookRepository.findByCompanyIdAndActiveTrue(companyId);
        if (webhooks.isEmpty()) {
            return;
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("eventId", event.getEventId());
        payload.put("shipmentId", event.getShipmentId());
        payload.put("eventType", event.getEventType());
        payload.put("timestamp", event.getEventTimestamp());

        for (Webhook webhook : webhooks) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                ResponseEntity<String> webhookResponse = restTemplate.postForEntity(webhook.getTargetUrl(),
                        new HttpEntity<>(payload, headers), String.class);
                webhookDeliveryLogRepository.save(
                        WebhookDeliveryLog.success(
                                webhook,
                                event,
                                webhookResponse.getStatusCodeValue(),
                                webhookResponse.getBody()
                        ));
            } catch (Exception ex) {
                webhookDeliveryLogRepository.save(WebhookDeliveryLog.failed(webhook, event, ex.getMessage()));
            }
        }
    }
}

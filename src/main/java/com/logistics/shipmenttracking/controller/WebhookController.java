package com.logistics.shipmenttracking.controller;

import com.logistics.shipmenttracking.dto.CreateWebhookRequest;
import com.logistics.shipmenttracking.dto.WebhookResponse;
import com.logistics.shipmenttracking.security.SecurityUtils;
import com.logistics.shipmenttracking.service.WebhookService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/webhooks")
@Validated
public class WebhookController {

    private final WebhookService webhookService;

    public WebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WebhookResponse create(@Valid @RequestBody CreateWebhookRequest request) {
        return webhookService.create(SecurityUtils.companyId(), request);
    }

    @DeleteMapping("/{webhookId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID webhookId) {
        webhookService.delete(SecurityUtils.companyId(), webhookId);
    }
}

package com.logistics.shipmenttracking.repository;

import com.logistics.shipmenttracking.domain.Webhook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WebhookRepository extends JpaRepository<Webhook, Long> {
    List<Webhook> findByCompanyIdAndActiveTrue(String companyId);

    Optional<Webhook> findByWebhookIdAndCompanyId(UUID webhookId, String companyId);
}

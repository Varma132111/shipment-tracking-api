package com.logistics.shipmenttracking.repository;

import com.logistics.shipmenttracking.domain.WebhookDeliveryLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebhookDeliveryLogRepository extends JpaRepository<WebhookDeliveryLog, Long> {
}

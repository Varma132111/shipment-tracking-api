package com.logistics.shipmenttracking.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistics.shipmenttracking.domain.Shipment;
import com.logistics.shipmenttracking.domain.ShipmentEvent;
import com.logistics.shipmenttracking.domain.ShipmentStatus;
import com.logistics.shipmenttracking.dto.CreateShipmentEventRequest;
import com.logistics.shipmenttracking.dto.ShipmentEventResponse;
import com.logistics.shipmenttracking.dto.ShipmentStatusResponse;
import com.logistics.shipmenttracking.exception.ApiException;
import com.logistics.shipmenttracking.repository.ShipmentEventRepository;
import com.logistics.shipmenttracking.repository.ShipmentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final ShipmentEventRepository shipmentEventRepository;
    private final WebhookService webhookService;
    private final ObjectMapper objectMapper;

    public ShipmentService(ShipmentRepository shipmentRepository,
                           ShipmentEventRepository shipmentEventRepository,
                           WebhookService webhookService,
                           ObjectMapper objectMapper) {
        this.shipmentRepository = shipmentRepository;
        this.shipmentEventRepository = shipmentEventRepository;
        this.webhookService = webhookService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public ShipmentEventResponse addEvent(String companyId, String shipmentId, CreateShipmentEventRequest request) {
        ShipmentStatus status = parseStatus(request.getEventType());
        Shipment shipment = shipmentRepository.findByCompanyIdAndShipmentId(companyId, shipmentId)
                .orElseGet(() -> createShipment(companyId, shipmentId));

        String locationJson = serializeAsJson(request.getLocation());
        String metadataJson = serializeAsJson(request.getMetadata());

        ShipmentEvent event = new ShipmentEvent(
                UUID.randomUUID(),
                companyId,
                shipmentId,
                status.name(),
                request.getTimestamp(),
                locationJson,
                metadataJson
        );

        shipmentEventRepository.save(event);
        shipment.setCurrentStatus(status);
        shipment.setLatestLocation(locationJson);
        shipment.setEta(extractEta(request.getMetadata()));
        shipment.setCondition(extractCondition(request.getMetadata()));
        shipmentRepository.save(shipment);

        webhookService.notifyStatusChange(companyId, event);

        return new ShipmentEventResponse(event.getEventId(), shipmentId, event.getEventType(), event.getEventTimestamp());
    }

    public Page<ShipmentEventResponse> getEvents(String companyId, String shipmentId, Pageable pageable) {
        return shipmentEventRepository.findByCompanyIdAndShipmentIdOrderByEventTimestampDesc(companyId, shipmentId, pageable)
                .map(event -> new ShipmentEventResponse(
                        event.getEventId(),
                        event.getShipmentId(),
                        event.getEventType(),
                        event.getEventTimestamp()
                ));
    }

    public ShipmentStatusResponse getStatus(String companyId, String shipmentId) {
        Shipment shipment = shipmentRepository.findByCompanyIdAndShipmentId(companyId, shipmentId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "SHIPMENT_NOT_FOUND", "Shipment not found"));
        return new ShipmentStatusResponse(
                shipment.getShipmentId(),
                shipment.getCurrentStatus().name(),
                shipment.getLatestLocation(),
                shipment.getEta(),
                shipment.getCondition()
        );
    }

    private Shipment createShipment(String companyId, String shipmentId) {
        Shipment shipment = new Shipment();
        shipment.setCompanyId(companyId);
        shipment.setShipmentId(shipmentId);
        shipment.setCurrentStatus(ShipmentStatus.PICKUP);
        return shipmentRepository.save(shipment);
    }

    private ShipmentStatus parseStatus(String eventType) {
        try {
            return ShipmentStatus.valueOf(eventType.toUpperCase());
        } catch (Exception ex) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "INVALID_EVENT_TYPE", "Invalid event type");
        }
    }

    private String serializeAsJson(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "INVALID_JSON", "Unable to parse request payload");
        }
    }

    private OffsetDateTime extractEta(Map<String, Object> metadata) {
        if (metadata == null || metadata.get("eta") == null) {
            return null;
        }
        try {
            return OffsetDateTime.parse(metadata.get("eta").toString());
        } catch (Exception ex) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "INVALID_ETA", "metadata.eta must be ISO-8601 date-time");
        }
    }

    private String extractCondition(Map<String, Object> metadata) {
        if (metadata == null) {
            return null;
        }
        Object value = metadata.get("condition");
        return value == null ? null : value.toString();
    }
}

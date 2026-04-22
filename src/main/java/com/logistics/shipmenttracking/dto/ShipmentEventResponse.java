package com.logistics.shipmenttracking.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public class ShipmentEventResponse {

    private UUID eventId;
    private String shipmentId;
    private String eventType;
    private OffsetDateTime timestamp;

    public ShipmentEventResponse(UUID eventId, String shipmentId, String eventType, OffsetDateTime timestamp) {
        this.eventId = eventId;
        this.shipmentId = shipmentId;
        this.eventType = eventType;
        this.timestamp = timestamp;
    }

    public UUID getEventId() {
        return eventId;
    }

    public String getShipmentId() {
        return shipmentId;
    }

    public String getEventType() {
        return eventType;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }
}

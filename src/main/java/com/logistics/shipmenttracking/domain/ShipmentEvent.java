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
@Table(name = "shipment_events")
public class ShipmentEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "event_id", nullable = false, unique = true)
    private UUID eventId;

    @Column(name = "company_id", nullable = false, length = 64)
    private String companyId;

    @Column(name = "shipment_id", nullable = false, length = 64)
    private String shipmentId;

    @Column(name = "event_type", nullable = false, length = 32)
    private String eventType;

    @Column(name = "event_timestamp", nullable = false)
    private OffsetDateTime eventTimestamp;

    @Column(name = "location", columnDefinition = "TEXT")
    private String location;

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    public ShipmentEvent() {
    }

    public ShipmentEvent(UUID eventId, String companyId, String shipmentId, String eventType, OffsetDateTime eventTimestamp, String location, String metadata) {
        this.eventId = eventId;
        this.companyId = companyId;
        this.shipmentId = shipmentId;
        this.eventType = eventType;
        this.eventTimestamp = eventTimestamp;
        this.location = location;
        this.metadata = metadata;
        this.createdAt = OffsetDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public UUID getEventId() {
        return eventId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public String getShipmentId() {
        return shipmentId;
    }

    public String getEventType() {
        return eventType;
    }

    public OffsetDateTime getEventTimestamp() {
        return eventTimestamp;
    }

    public String getLocation() {
        return location;
    }

    public String getMetadata() {
        return metadata;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}

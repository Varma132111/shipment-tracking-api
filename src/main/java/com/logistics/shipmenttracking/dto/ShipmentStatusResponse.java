package com.logistics.shipmenttracking.dto;

import java.time.OffsetDateTime;

public class ShipmentStatusResponse {
    private String shipmentId;
    private String status;
    private String latestLocation;
    private OffsetDateTime eta;
    private String condition;

    public ShipmentStatusResponse(String shipmentId, String status, String latestLocation, OffsetDateTime eta, String condition) {
        this.shipmentId = shipmentId;
        this.status = status;
        this.latestLocation = latestLocation;
        this.eta = eta;
        this.condition = condition;
    }

    public String getShipmentId() {
        return shipmentId;
    }

    public String getStatus() {
        return status;
    }

    public String getLatestLocation() {
        return latestLocation;
    }

    public OffsetDateTime getEta() {
        return eta;
    }

    public String getCondition() {
        return condition;
    }
}

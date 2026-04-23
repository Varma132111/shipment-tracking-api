package com.logistics.shipmenttracking.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistics.shipmenttracking.domain.Shipment;
import com.logistics.shipmenttracking.domain.ShipmentEvent;
import com.logistics.shipmenttracking.domain.ShipmentStatus;
import com.logistics.shipmenttracking.dto.CreateShipmentEventRequest;
import com.logistics.shipmenttracking.dto.LocationDto;
import com.logistics.shipmenttracking.repository.ShipmentEventRepository;
import com.logistics.shipmenttracking.repository.ShipmentRepository;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ShipmentServiceTest {

    @Test
    void addEvent_savesAndReturns() {
        ShipmentRepository shipments = mock(ShipmentRepository.class);
        ShipmentEventRepository events = mock(ShipmentEventRepository.class);
        WebhookService webhooks = mock(WebhookService.class);
        ShipmentService svc = new ShipmentService(shipments, events, webhooks, new ObjectMapper());

        Shipment row = new Shipment();
        row.setCompanyId("acme");
        row.setShipmentId("SHP-1");
        row.setCurrentStatus(ShipmentStatus.PICKUP);
        when(shipments.findByCompanyIdAndShipmentId("acme", "SHP-1")).thenReturn(Optional.of(row));
        when(events.save(any(ShipmentEvent.class))).thenAnswer(i -> i.getArgument(0));
        when(shipments.save(any(Shipment.class))).thenAnswer(i -> i.getArgument(0));

        var req = new CreateShipmentEventRequest();
        req.setEventType("IN_TRANSIT");
        req.setTimestamp(OffsetDateTime.parse("2026-04-17T14:30:00Z"));
        var loc = new LocationDto();
        loc.setLatitude(1.0);
        loc.setLongitude(2.0);
        loc.setAddress("Here");
        req.setLocation(loc);
        req.setMetadata(Map.of("condition", "OK"));

        var out = svc.addEvent("acme", "SHP-1", req);

        assertThat(out.getShipmentId()).isEqualTo("SHP-1");
        assertThat(out.getEventType()).isEqualTo("IN_TRANSIT");
        verify(events).save(any(ShipmentEvent.class));
        verify(shipments).save(any(Shipment.class));
        verify(webhooks).notifyStatusChange(eq("acme"), any(ShipmentEvent.class));
    }
}

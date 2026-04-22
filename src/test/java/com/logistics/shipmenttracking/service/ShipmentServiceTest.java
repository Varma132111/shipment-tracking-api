package com.logistics.shipmenttracking.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistics.shipmenttracking.domain.Shipment;
import com.logistics.shipmenttracking.domain.ShipmentEvent;
import com.logistics.shipmenttracking.domain.ShipmentStatus;
import com.logistics.shipmenttracking.dto.CreateShipmentEventRequest;
import com.logistics.shipmenttracking.dto.LocationDto;
import com.logistics.shipmenttracking.dto.ShipmentEventResponse;
import com.logistics.shipmenttracking.exception.ApiException;
import com.logistics.shipmenttracking.repository.ShipmentEventRepository;
import com.logistics.shipmenttracking.repository.ShipmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShipmentServiceTest {

    @Mock
    private ShipmentRepository shipmentRepository;

    @Mock
    private ShipmentEventRepository shipmentEventRepository;

    @Mock
    private WebhookService webhookService;

    @Test
    void addEvent_shouldPersistEventAndUpdateShipment() {
        ShipmentService shipmentService = new ShipmentService(
                shipmentRepository,
                shipmentEventRepository,
                webhookService,
                new ObjectMapper()
        );

        Shipment shipment = new Shipment();
        shipment.setCompanyId("acme");
        shipment.setShipmentId("SHP-1");
        shipment.setCurrentStatus(ShipmentStatus.PICKUP);

        when(shipmentRepository.findByCompanyIdAndShipmentId("acme", "SHP-1")).thenReturn(Optional.of(shipment));
        when(shipmentEventRepository.save(any(ShipmentEvent.class))).thenAnswer(i -> i.getArgument(0));
        when(shipmentRepository.save(any(Shipment.class))).thenAnswer(i -> i.getArgument(0));

        CreateShipmentEventRequest request = new CreateShipmentEventRequest();
        request.setEventType("IN_TRANSIT");
        request.setTimestamp(OffsetDateTime.parse("2026-04-17T14:30:00Z"));
        LocationDto location = new LocationDto();
        location.setLatitude(40.0);
        location.setLongitude(-74.0);
        location.setAddress("NY");
        request.setLocation(location);
        request.setMetadata(Map.of("condition", "GOOD"));

        ShipmentEventResponse response = shipmentService.addEvent("acme", "SHP-1", request);

        assertThat(response.getShipmentId()).isEqualTo("SHP-1");
        assertThat(response.getEventType()).isEqualTo("IN_TRANSIT");

        ArgumentCaptor<Shipment> shipmentCaptor = ArgumentCaptor.forClass(Shipment.class);
        verify(shipmentRepository, atLeastOnce()).save(shipmentCaptor.capture());
        assertThat(shipmentCaptor.getValue().getCurrentStatus()).isEqualTo(ShipmentStatus.IN_TRANSIT);
    }

    @Test
    void addEvent_shouldThrowBadRequestWhenEtaIsInvalid() {
        ShipmentService shipmentService = new ShipmentService(
                shipmentRepository,
                shipmentEventRepository,
                webhookService,
                new ObjectMapper()
        );

        Shipment shipment = new Shipment();
        shipment.setCompanyId("acme");
        shipment.setShipmentId("SHP-1");
        shipment.setCurrentStatus(ShipmentStatus.PICKUP);
        when(shipmentRepository.findByCompanyIdAndShipmentId("acme", "SHP-1")).thenReturn(Optional.of(shipment));
        when(shipmentEventRepository.save(any(ShipmentEvent.class))).thenAnswer(i -> i.getArgument(0));

        CreateShipmentEventRequest request = new CreateShipmentEventRequest();
        request.setEventType("IN_TRANSIT");
        request.setTimestamp(OffsetDateTime.parse("2026-04-17T14:30:00Z"));
        LocationDto location = new LocationDto();
        location.setLatitude(40.0);
        location.setLongitude(-74.0);
        location.setAddress("NY");
        request.setLocation(location);
        request.setMetadata(Map.of("eta", "not-a-date"));

        assertThatThrownBy(() -> shipmentService.addEvent("acme", "SHP-1", request))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("metadata.eta must be ISO-8601 date-time");
    }
}

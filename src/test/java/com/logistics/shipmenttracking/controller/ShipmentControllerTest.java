package com.logistics.shipmenttracking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.logistics.shipmenttracking.dto.CreateShipmentEventRequest;
import com.logistics.shipmenttracking.dto.LocationDto;
import com.logistics.shipmenttracking.dto.ShipmentEventResponse;
import com.logistics.shipmenttracking.dto.ShipmentStatusResponse;
import com.logistics.shipmenttracking.exception.GlobalExceptionHandler;
import com.logistics.shipmenttracking.security.CurrentUser;
import com.logistics.shipmenttracking.service.ShipmentService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ShipmentControllerTest {

    private MockMvc mockMvc;
    private ShipmentService shipmentService;

    private static final ObjectMapper JSON = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @BeforeEach
    void setUp() {
        shipmentService = mock(ShipmentService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new ShipmentController(shipmentService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(new CurrentUser("acme"), null, List.of()));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getStatus_ok() throws Exception {
        when(shipmentService.getStatus("acme", "SHP-1"))
                .thenReturn(new ShipmentStatusResponse("SHP-1", "IN_TRANSIT", "{}", null, null));

        mockMvc.perform(get("/api/v1/shipments/SHP-1/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_TRANSIT"));
    }

    @Test
    void addEvent_created() throws Exception {
        var ts = OffsetDateTime.parse("2026-04-17T14:30:00Z");
        var loc = new LocationDto();
        loc.setLatitude(40.0);
        loc.setLongitude(-74.0);
        loc.setAddress("NY");
        var body = new CreateShipmentEventRequest();
        body.setEventType("IN_TRANSIT");
        body.setTimestamp(ts);
        body.setLocation(loc);

        when(shipmentService.addEvent(eq("acme"), eq("SHP-1"), any(CreateShipmentEventRequest.class)))
                .thenReturn(new ShipmentEventResponse(UUID.randomUUID(), "SHP-1", "IN_TRANSIT", ts));

        mockMvc.perform(post("/api/v1/shipments/SHP-1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shipmentId").value("SHP-1"));
    }
}

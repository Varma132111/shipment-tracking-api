package com.logistics.shipmenttracking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ShipmentControllerTest {

    private MockMvc mockMvc;
    private ShipmentService shipmentService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        shipmentService = mock(ShipmentService.class);
        ShipmentController controller = new ShipmentController(shipmentService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        CurrentUser currentUser = new CurrentUser("acme");
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(currentUser, null, List.of()));
    }

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void addEvent_shouldReturnCreated() throws Exception {
        CreateShipmentEventRequest request = new CreateShipmentEventRequest();
        request.setEventType("IN_TRANSIT");
        request.setTimestamp(OffsetDateTime.parse("2026-04-17T14:30:00Z"));
        LocationDto location = new LocationDto();
        location.setLatitude(40.0);
        location.setLongitude(-74.0);
        location.setAddress("NY");
        request.setLocation(location);

        when(shipmentService.addEvent(anyString(), eq("SHP-1"), any(CreateShipmentEventRequest.class)))
                .thenReturn(new ShipmentEventResponse(UUID.randomUUID(), "SHP-1", "IN_TRANSIT", request.getTimestamp()));

        mockMvc.perform(post("/api/v1/shipments/SHP-1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shipmentId").value("SHP-1"));
    }

    @Test
    void getStatus_shouldReturn200() throws Exception {
        when(shipmentService.getStatus("acme", "SHP-1"))
                .thenReturn(new ShipmentStatusResponse("SHP-1", "IN_TRANSIT", "{\"address\":\"NY\"}", null, "GOOD"));

        mockMvc.perform(get("/api/v1/shipments/SHP-1/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_TRANSIT"));
    }

    @Test
    void getEvents_shouldReturn200() throws Exception {
        when(shipmentService.getEvents(eq("acme"), eq("SHP-1"), any()))
                .thenReturn(new PageImpl<>(List.of(
                        new ShipmentEventResponse(UUID.randomUUID(), "SHP-1", "PICKUP", OffsetDateTime.now())
                )));

        mockMvc.perform(get("/api/v1/shipments/SHP-1/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].shipmentId").value("SHP-1"));
    }

    @Test
    void addEvent_shouldReturn400ForInvalidPayload() throws Exception {
        mockMvc.perform(post("/api/v1/shipments/SHP-1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void getStatus_shouldReturn401WhenNoAuthenticationContext() throws Exception {
        SecurityContextHolder.clearContext();

        mockMvc.perform(get("/api/v1/shipments/SHP-1/status"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }
}

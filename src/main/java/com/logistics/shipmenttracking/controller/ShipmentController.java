package com.logistics.shipmenttracking.controller;

import com.logistics.shipmenttracking.dto.CreateShipmentEventRequest;
import com.logistics.shipmenttracking.dto.ShipmentEventResponse;
import com.logistics.shipmenttracking.dto.ShipmentStatusResponse;
import com.logistics.shipmenttracking.security.SecurityUtils;
import com.logistics.shipmenttracking.service.ShipmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@RestController
@RequestMapping("/api/v1/shipments")
@Validated
public class ShipmentController {

    private final ShipmentService shipmentService;

    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @PostMapping("/{shipmentId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public ShipmentEventResponse addEvent(@PathVariable String shipmentId,
                                          @Valid @RequestBody CreateShipmentEventRequest request) {
        return shipmentService.addEvent(SecurityUtils.companyId(), shipmentId, request);
    }

    @GetMapping("/{shipmentId}/events")
    public Page<ShipmentEventResponse> getPastEvents(@PathVariable String shipmentId,
                                                     @RequestParam(defaultValue = "0") @Min(0) int page,
                                                     @RequestParam(defaultValue = "50") @Min(1) @Max(200) int size) {
        Pageable pageable = PageRequest.of(page, size);
        return shipmentService.getEvents(SecurityUtils.companyId(), shipmentId, pageable);
    }

    @GetMapping("/{shipmentId}/status")
    public ShipmentStatusResponse getStatus(@PathVariable String shipmentId) {
        return shipmentService.getStatus(SecurityUtils.companyId(), shipmentId);
    }
}

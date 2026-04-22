package com.logistics.shipmenttracking.repository;

import com.logistics.shipmenttracking.domain.ShipmentEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShipmentEventRepository extends JpaRepository<ShipmentEvent, Long> {
    Page<ShipmentEvent> findByCompanyIdAndShipmentIdOrderByEventTimestampDesc(String companyId, String shipmentId, Pageable pageable);
}

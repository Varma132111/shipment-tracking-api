package com.logistics.shipmenttracking.repository;

import com.logistics.shipmenttracking.domain.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    Optional<Shipment> findByCompanyIdAndShipmentId(String companyId, String shipmentId);
}

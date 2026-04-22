package com.logistics.shipmenttracking.repository;

import com.logistics.shipmenttracking.domain.ApiRateLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;

public interface ApiRateLimitRepository extends JpaRepository<ApiRateLimit, Long> {
    @Modifying
    @Query(value = "INSERT INTO api_rate_limits (company_id, window_start, request_count, updated_at) " +
            "VALUES (:companyId, :windowStart, 0, now()) " +
            "ON CONFLICT (company_id, window_start) DO NOTHING", nativeQuery = true)
    void ensureWindowExists(@Param("companyId") String companyId, @Param("windowStart") OffsetDateTime windowStart);

    @Modifying
    @Query(value = "UPDATE api_rate_limits " +
            "SET request_count = request_count + 1, updated_at = now() " +
            "WHERE company_id = :companyId " +
            "AND window_start = :windowStart " +
            "AND request_count < :maxRequests", nativeQuery = true)
    int incrementIfBelowLimit(@Param("companyId") String companyId,
                              @Param("windowStart") OffsetDateTime windowStart,
                              @Param("maxRequests") int maxRequests);
}

package com.logistics.shipmenttracking.domain;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "api_rate_limits")
public class ApiRateLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_id", nullable = false)
    private String companyId;

    @Column(name = "window_start", nullable = false)
    private OffsetDateTime windowStart;

    @Column(name = "request_count", nullable = false)
    private Integer requestCount;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public ApiRateLimit() {
    }

    public ApiRateLimit(String companyId, OffsetDateTime windowStart) {
        this.companyId = companyId;
        this.windowStart = windowStart;
        this.requestCount = 0;
        this.updatedAt = OffsetDateTime.now();
    }

    public Integer getRequestCount() {
        return requestCount;
    }

    public void increment() {
        this.requestCount = this.requestCount + 1;
        this.updatedAt = OffsetDateTime.now();
    }
}

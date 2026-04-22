package com.logistics.shipmenttracking.service;

import com.logistics.shipmenttracking.exception.ApiException;
import com.logistics.shipmenttracking.repository.ApiRateLimitRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Service
public class ApiRateLimitService {

    private final ApiRateLimitRepository apiRateLimitRepository;
    private final int maxRequestsPerMinute;

    public ApiRateLimitService(ApiRateLimitRepository apiRateLimitRepository,
                               @Value("${app.rate-limit.requests-per-minute}") int maxRequestsPerMinute) {
        this.apiRateLimitRepository = apiRateLimitRepository;
        this.maxRequestsPerMinute = maxRequestsPerMinute;
    }

    @Transactional
    public void checkAndIncrement(String companyId) {
        OffsetDateTime windowStart = OffsetDateTime.now(ZoneOffset.UTC)
                .withSecond(0)
                .withNano(0);

        apiRateLimitRepository.ensureWindowExists(companyId, windowStart);
        int updatedRows = apiRateLimitRepository.incrementIfBelowLimit(companyId, windowStart, maxRequestsPerMinute);
        if (updatedRows == 0) {
            throw new ApiException(HttpStatus.TOO_MANY_REQUESTS, "RATE_LIMIT_EXCEEDED",
                    "Rate limit exceeded for this company");
        }
    }
}

package com.logistics.shipmenttracking.service;

import com.logistics.shipmenttracking.exception.ApiException;
import com.logistics.shipmenttracking.repository.ApiRateLimitRepository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ApiRateLimitServiceTest {

    @Test
    void overLimit_throws() {
        ApiRateLimitRepository repo = mock(ApiRateLimitRepository.class);
        when(repo.incrementIfBelowLimit(anyString(), any(), anyInt())).thenReturn(0);

        ApiRateLimitService svc = new ApiRateLimitService(repo, 5);

        assertThatThrownBy(() -> svc.checkAndIncrement("acme")).isInstanceOf(ApiException.class);
    }
}

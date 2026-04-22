package com.logistics.shipmenttracking.service;

import com.logistics.shipmenttracking.exception.ApiException;
import com.logistics.shipmenttracking.repository.ApiRateLimitRepository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ApiRateLimitServiceTest {

    @Test
    void checkAndIncrement_shouldAllowRequestWhenBelowLimit() {
        ApiRateLimitRepository repository = mock(ApiRateLimitRepository.class);
        ApiRateLimitService service = new ApiRateLimitService(repository, 10);

        when(repository.incrementIfBelowLimit(anyString(), any(), anyInt())).thenReturn(1);

        assertThatCode(() -> service.checkAndIncrement("acme")).doesNotThrowAnyException();
        verify(repository).ensureWindowExists(anyString(), any());
    }

    @Test
    void checkAndIncrement_shouldThrowWhenLimitReached() {
        ApiRateLimitRepository repository = mock(ApiRateLimitRepository.class);
        ApiRateLimitService service = new ApiRateLimitService(repository, 1);

        when(repository.incrementIfBelowLimit(anyString(), any(), anyInt())).thenReturn(0);

        assertThatThrownBy(() -> service.checkAndIncrement("acme"))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Rate limit exceeded");

        verify(repository).ensureWindowExists(anyString(), any());
    }
}

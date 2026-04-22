package com.logistics.shipmenttracking.config;

import com.logistics.shipmenttracking.security.SecurityUtils;
import com.logistics.shipmenttracking.service.ApiRateLimitService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final ApiRateLimitService apiRateLimitService;

    public RateLimitInterceptor(ApiRateLimitService apiRateLimitService) {
        this.apiRateLimitService = apiRateLimitService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        apiRateLimitService.checkAndIncrement(SecurityUtils.companyId());
        return true;
    }
}

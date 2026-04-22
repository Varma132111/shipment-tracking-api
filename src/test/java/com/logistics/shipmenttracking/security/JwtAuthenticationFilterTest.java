package com.logistics.shipmenttracking.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class JwtAuthenticationFilterTest {

    private static final String SECRET = "my-super-secret-key-my-super-secret-key";

    @Test
    void doFilter_shouldSetAuthenticationForValidToken() throws Exception {
        JwtService jwtService = new JwtService(SECRET);
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService);

        String token = Jwts.builder()
                .setSubject("dev-user")
                .claim("companyId", "acme")
                .setExpiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(SignatureAlgorithm.HS256, SECRET.getBytes(StandardCharsets.UTF_8))
                .compact();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        assertThat(principal).isInstanceOf(CurrentUser.class);
        assertThat(((CurrentUser) principal).getCompanyId()).isEqualTo("acme");
    }

    @Test
    void doFilter_shouldReturn401ForInvalidToken() throws Exception {
        JwtService jwtService = new JwtService(SECRET);
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer not-a-valid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("\"code\":\"UNAUTHORIZED\"");
        assertThat(response.getContentAsString()).contains("\"details\":{}");
        assertThat(response.getContentAsString()).contains("\"timestamp\":");
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilter_shouldPassThroughWhenNoAuthorizationHeader() throws Exception {
        JwtService jwtService = new JwtService(SECRET);
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilter_shouldReturn401WhenCompanyIdClaimMissing() throws Exception {
        JwtService jwtService = new JwtService(SECRET);
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService);

        String token = Jwts.builder()
                .setSubject("dev-user")
                .setExpiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(SignatureAlgorithm.HS256, SECRET.getBytes(StandardCharsets.UTF_8))
                .compact();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("Missing required JWT claims");
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}

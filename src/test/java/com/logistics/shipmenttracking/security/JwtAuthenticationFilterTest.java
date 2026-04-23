package com.logistics.shipmenttracking.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.AfterEach;
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

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void goodBearer_setsUser() throws Exception {
        var jwt = new JwtService(SECRET);
        var filter = new JwtAuthenticationFilter(jwt);
        String token = Jwts.builder()
                .setSubject("u1")
                .claim("companyId", "acme")
                .setExpiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(SignatureAlgorithm.HS256, SECRET.getBytes(StandardCharsets.UTF_8))
                .compact();

        var req = new MockHttpServletRequest();
        req.addHeader("Authorization", "Bearer " + token);
        var res = new MockHttpServletResponse();

        filter.doFilter(req, res, new MockFilterChain());

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .isInstanceOfSatisfying(CurrentUser.class, u -> assertThat(u.getCompanyId()).isEqualTo("acme"));
    }

    @Test
    void badBearer_401() throws Exception {
        var filter = new JwtAuthenticationFilter(new JwtService(SECRET));
        var req = new MockHttpServletRequest();
        req.addHeader("Authorization", "Bearer garbage");
        var res = new MockHttpServletResponse();

        filter.doFilter(req, res, new MockFilterChain());

        assertThat(res.getStatus()).isEqualTo(401);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}

package com.mocktestpro.config;

import com.mocktestpro.module.user.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class KeycloakUserSyncFilter extends OncePerRequestFilter {

    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        log.info("=== KeycloakUserSyncFilter called ===");

        var authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("Authentication: {}", authentication);

        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            String keycloakId = jwt.getSubject();
            String email = jwt.getClaimAsString("email");
            String firstName = jwt.getClaimAsString("given_name");
            String lastName = jwt.getClaimAsString("family_name");

            log.info("Syncing user: keycloakId={}, email={}, firstName={}, lastName={}",
                    keycloakId, email, firstName, lastName);

            // Create user if not exists
            userService.findOrCreateUser(keycloakId, email, firstName, lastName);
        }

        filterChain.doFilter(request, response);
    }
}
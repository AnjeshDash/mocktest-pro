package com.mocktestpro.common.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public String getCurrentKeycloakId() {
        return getJwt().getSubject();
    }

    public String getCurrentEmail() {
        return getJwt().getClaimAsString("email");
    }

    public boolean hasRole(String role) {
        return SecurityContextHolder.getContext()
                .getAuthentication().getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(role));
    }

    public boolean isAdmin() {
        return hasRole("ROLE_ADMIN");
    }

    public boolean isOrganizer() {
        return hasRole("ROLE_ORGANIZER");
    }

    public boolean isAttendee() {
        return hasRole("ROLE_ATTENDEE");
    }

    private Jwt getJwt() {
        return (Jwt) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
    }
}
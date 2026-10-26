package org.optipace.adminService.service.impl;

import lombok.RequiredArgsConstructor;
import org.optipace.adminService.dto.response.FactoryScope;
import org.optipace.adminService.repository.FactoryAccessRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FactoryContextService {

    private final FactoryAccessRepository factoryAccessRepository;

    public FactoryScope getCurrentFactoryScope() {

        var authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null
                || authentication.getName().isBlank()) {
            throw new AccessDeniedException("User is not authenticated");
        }

        String userId = authentication.getName();

        FactoryScope scope =
                factoryAccessRepository.findFactoryScopeByUserId(userId)
                        .orElseThrow(() ->
                                new AccessDeniedException(
                                        "No active Factory Admin assignment found"));

        // This repository must query your actual admin-to-factory mapping.
        return factoryAccessRepository.findFactoryScopeByUserId(userId)
                .orElseThrow(() ->
                        new AccessDeniedException(
                                "No factory assignment found for this user"));
    }
}
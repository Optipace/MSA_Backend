package org.optipace.adminService.repository;

import org.optipace.adminService.dto.response.FactoryScope;

import java.util.Optional;

public interface FactoryAccessRepository {

    Optional<FactoryScope> findFactoryScopeByUserId(String userId);

}

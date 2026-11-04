package org.optipace.masterService.repository;

import org.optipace.masterService.dto.response.FactoryScope;
import java.util.Optional;

public interface FactoryAccessRepository {

    Optional<FactoryScope> findFactoryScopeByUserId(String userId);

}

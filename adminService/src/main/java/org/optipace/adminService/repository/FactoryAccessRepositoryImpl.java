package org.optipace.adminService.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.optipace.adminService.dto.response.FactoryScope;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class FactoryAccessRepositoryImpl
        implements FactoryAccessRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<FactoryScope> findFactoryScopeByUserId(
            String userId) {

        final long employeeId;

        try {
            employeeId = Long.parseLong(userId);
        } catch (NumberFormatException ex) {
            throw new AccessDeniedException(
                    "Authenticated user ID is not a valid employee ID");
        }

        String sql = """
            SELECT DISTINCT
                e.organization_id,
                e.factory_id
            FROM master.employee e
            JOIN security.employee_role er
                ON er.employee_id = e.employee_id
            WHERE e.employee_id = :employeeId
              AND er.role_id = 2
              AND er.effective_from <= CURRENT_DATE
              AND (
                    er.effective_to IS NULL
                    OR er.effective_to >= CURRENT_DATE
              )
              AND e.record_status = 'A'
            """;

        @SuppressWarnings("unchecked")
        List<Object[]> rows = entityManager
                .createNativeQuery(sql)
                .setParameter("employeeId", employeeId)
                .getResultList();

        if (rows.isEmpty()) {
            return Optional.empty();
        }

        if (rows.size() > 1) {
            throw new AccessDeniedException(
                    "Multiple factory assignments found for this user");
        }

        Object[] row = rows.get(0);

        Long organisationId = ((Number) row[0]).longValue();
        Long factoryId = ((Number) row[1]).longValue();

        return Optional.of(
                new FactoryScope(organisationId, factoryId));
    }
}
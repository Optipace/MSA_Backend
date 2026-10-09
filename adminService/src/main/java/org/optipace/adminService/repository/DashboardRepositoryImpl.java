package org.optipace.adminService.repository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.optipace.adminService.dto.response.DashboardSummaryProjection;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public class DashboardRepositoryImpl implements DashboardRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public DashboardSummaryProjection getDashboardSummary() {

        Object[] row = (Object[]) entityManager.createNativeQuery("""
            SELECT
                (SELECT COUNT(*)
                 FROM master.employee)
                    AS total_employees_assessed,

                (SELECT COUNT(*)
                 FROM assessment.assessment_session
                 WHERE assessment_status_id = 3)
                    AS tests_taken,

                (SELECT COUNT(*)
                 FROM assessment.assessment_session
                 WHERE assessment_status_id IN (1, 2))
                    AS tests_pending,

                (SELECT COUNT(*)
                 FROM assessment.final_score
                 WHERE overall_result = 'FAIL')
                    AS tests_failed,

                (SELECT COUNT(*)
                 FROM assessment.final_score
                 WHERE overall_result = 'PASS')
                    AS good_performance
            """).getSingleResult();

        return new DashboardSummaryProjection() {
            @Override
            public Long getTotalEmployeesAssessed() {
                return ((Number) row[0]).longValue();
            }

            @Override
            public Long getTestsTaken() {
                return ((Number) row[1]).longValue();
            }

            @Override
            public Long getTestsPending() {
                return ((Number) row[2]).longValue();
            }

            @Override
            public Long getTestsFailed() {
                return ((Number) row[3]).longValue();
            }

            @Override
            public Long getGoodPerformance() {
                return ((Number) row[4]).longValue();
            }
        };
    }
}
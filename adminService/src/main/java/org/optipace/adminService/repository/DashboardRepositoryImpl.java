package org.optipace.adminService.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.optipace.adminService.dto.response.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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

    @Override
    public List<RecentAssessmentResponse> getRecentCompletedAssessments(
            int page, int size) {

        int offset = page * size;

        List<Object[]> rows = entityManager.createNativeQuery("""
                        SELECT
                            e.employee_code,
                            CONCAT_WS(' ', e.first_name, e.middle_name, e.last_name),
                            d.department_name,
                            am.module_name,
                            s.total_score,
                            ast.status_name,
                            s.completed_on
                        FROM assessment.assessment_session s
                        JOIN assessment.assessment_status ast
                            ON s.assessment_status_id = ast.assessment_status_id
                        JOIN master.employee e
                            ON s.employee_id = e.employee_id
                        JOIN master.department d
                            ON e.department_id = d.department_id
                        LEFT JOIN assessment.assessment_attempt aa
                            ON aa.assessment_session_id = s.assessment_session_id
                        LEFT JOIN assessment.assessment_module_instance ami
                            ON ami.assessment_attempt_id = aa.assessment_attempt_id
                        LEFT JOIN config.assessment_module am
                            ON am.assessment_module_id = ami.assessment_module_id
                        WHERE ast.status_code = 'COMPLETED'
                          AND s.completed_on IS NOT NULL
                        ORDER BY s.completed_on DESC, s.assessment_session_id DESC
                        LIMIT :size OFFSET :offset
                        """)
                .setParameter("size", size)
                .setParameter("offset", offset)
                .getResultList();

        return rows.stream()
                .map(row -> new RecentAssessmentResponse(
                        row[0] == null ? null : row[0].toString(),
                        row[1] == null ? null : row[1].toString(),
                        row[2] == null ? null : row[2].toString(),
                        row[3] == null ? null : row[3].toString(),
                        row[4] == null ? null
                                : new java.math.BigDecimal(row[4].toString()),
                        row[5] == null ? null : row[5].toString(),
                        row[6] == null ? null : row[6].toString()
                ))
                .toList();
    }

    @Override
    public long countRecentCompletedAssessments() {

        Number result = (Number) entityManager.createNativeQuery("""
                        SELECT COUNT(DISTINCT s.assessment_session_id)
                        FROM assessment.assessment_session s
                        JOIN assessment.assessment_status ast
                            ON s.assessment_status_id = ast.assessment_status_id
                        WHERE ast.status_code = 'COMPLETED'
                          AND s.completed_on IS NOT NULL
                        """)
                .getSingleResult();

        return result.longValue();
    }

    @Override
    public List<CompetencyDistributionProjection> findCompetencyDistribution() {

        List<Object[]> rows = entityManager.createNativeQuery("""
                
                        SELECT
                    cr.competency_rating_id,
                    cr.rating_code,
                    cr.rating_name,
                    cr.color_code,
                    COUNT(DISTINCT ases.employee_id)
                FROM assessment.assessment_session ases
                INNER JOIN assessment.final_score fs
                    ON fs.assessment_session_id = ases.assessment_session_id
                INNER JOIN config.competency_rating cr
                    ON cr.competency_rating_id = fs.competency_rating_id
                WHERE fs.competency_rating_id IS NOT NULL
                GROUP BY
                    cr.competency_rating_id,
                    cr.rating_code,
                    cr.rating_name,
                    cr.color_code,
                    cr.display_order
                ORDER BY
                    cr.display_order NULLS LAST,
                    cr.competency_rating_id
                """).getResultList();

        return rows.stream()
                .map(row -> (CompetencyDistributionProjection)
                        new CompetencyDistributionProjection() {

                            @Override
                            public Long getCompetencyRatingId() {
                                return row[0] == null
                                        ? null : ((Number) row[0]).longValue();
                            }

                            @Override
                            public String getRatingCode() {
                                return row[1] == null
                                        ? null : row[1].toString();
                            }

                            @Override
                            public String getRatingName() {
                                return row[2] == null
                                        ? null : row[2].toString();
                            }

                            @Override
                            public String getColorCode() {
                                return row[3] == null
                                        ? null : row[3].toString();
                            }

                            @Override
                            public Long getEmployeeCount() {
                                return row[4] == null
                                        ? 0L : ((Number) row[4]).longValue();
                            }
                        })
                .toList();
    }

    @Override
    public Long countDistinctEmployeesWithCompetencyRating() {

        Number result = (Number) entityManager.createNativeQuery("""
                SELECT COUNT(DISTINCT ases.employee_id)
                FROM assessment.assessment_session ases
                INNER JOIN assessment.final_score fs
                    ON fs.assessment_session_id = ases.assessment_session_id
                WHERE fs.competency_rating_id IS NOT NULL
                """).getSingleResult();

        return result.longValue();
    }

    @Override
    public List<MsaTrendPointResponse> getMsaTrend(
            LocalDate startDate,
            LocalDate endDate) {

        List<Object[]> rows = entityManager.createNativeQuery("""
                        WITH months AS (
                            SELECT generate_series(
                                date_trunc('month', CAST(:startDate AS date)),
                                date_trunc('month', CAST(:endDate AS date)),
                                INTERVAL '1 month'
                            ) AS month_start
                        ),
                        monthly_scores AS (
                            SELECT
                                date_trunc('month', s.completed_on) AS month_start,
                                AVG(fs.percentage) AS average_score
                            FROM assessment.final_score fs
                            INNER JOIN assessment.assessment_session s
                                ON s.assessment_session_id =
                                   fs.assessment_session_id
                            INNER JOIN assessment.assessment_status ast
                                ON ast.assessment_status_id =
                                   s.assessment_status_id
                            WHERE ast.status_code = 'COMPLETED'
                              AND s.completed_on IS NOT NULL
                              AND fs.percentage IS NOT NULL
                              AND s.completed_on >= CAST(:startDate AS date)
                              AND s.completed_on <
                                  CAST(:endDate AS date) + INTERVAL '1 day'
                            GROUP BY date_trunc('month', s.completed_on)
                        )
                        SELECT
                            EXTRACT(YEAR FROM m.month_start),
                            EXTRACT(MONTH FROM m.month_start),
                            TO_CHAR(m.month_start, 'Mon YYYY'),
                            ms.average_score
                        FROM months m
                        LEFT JOIN monthly_scores ms
                            ON ms.month_start = m.month_start
                        ORDER BY m.month_start
                        """)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();

        return rows.stream()
                .map(row -> new MsaTrendPointResponse(
                        row[0] == null
                                ? null : ((Number) row[0]).intValue(),

                        row[1] == null
                                ? null : ((Number) row[1]).intValue(),

                        row[2] == null
                                ? null : row[2].toString(),

                        row[3] == null
                                ? null
                                : new java.math.BigDecimal(
                                row[3].toString()
                        ).setScale(
                                2,
                                java.math.RoundingMode.HALF_UP
                        )
                ))
                .toList();
    }


    @Override
    public List<TopMissedDefectProjection> getTopMissedDefects() {

        List<Object[]> rows = entityManager.createNativeQuery("""
                SELECT
                    dm.defect_name AS "defectName",
                
                    COUNT(*) FILTER (
                        WHERE gir.defect_correct = FALSE
                    ) AS "missedCount",
                
                    COUNT(*) AS "totalCount",
                
                    COUNT(DISTINCT CASE
                        WHEN gir.defect_correct = FALSE
                        THEN ases.employee_id
                    END) AS "employeeCount"
                
                FROM assessment.garment_identification_response gir
                
                JOIN assessment.assessment_response ar
                    ON ar.assessment_response_id =
                       gir.assessment_response_id
                
                JOIN assessment.assessment_module_instance ami
                    ON ami.assessment_module_instance_id =
                       ar.assessment_module_instance_id
                
                JOIN assessment.assessment_attempt aa
                    ON aa.assessment_attempt_id =
                       ami.assessment_attempt_id
                
                JOIN assessment.assessment_session ases
                    ON ases.assessment_session_id =
                       aa.assessment_session_id
                
                JOIN garment.garment_expected_defect ged
                    ON ged.garment_expected_defect_id =
                       gir.expected_defect_id
                
                JOIN garment.defect_master dm
                    ON dm.defect_id = ged.defect_id
                
                WHERE gir.expected_defect_id IS NOT NULL
                  AND gir.defect_correct IS NOT NULL
                
                GROUP BY dm.defect_id, dm.defect_name
                
                HAVING COUNT(*) FILTER (
                    WHERE gir.defect_correct = FALSE
                ) > 0
                
                ORDER BY
                    COUNT(*) FILTER (
                        WHERE gir.defect_correct = FALSE
                    ) * 100.0 / NULLIF(COUNT(*), 0) DESC,
                
                    COUNT(DISTINCT CASE
                        WHEN gir.defect_correct = FALSE
                        THEN ases.employee_id
                    END) DESC,
                
                    dm.defect_name ASC
                
                LIMIT 5
                """).getResultList();

        return rows.stream()
                .map(row -> (TopMissedDefectProjection) new TopMissedDefectProjection() {

                    @Override
                    public String getDefectName() {
                        return row[0] == null ? null : row[0].toString();
                    }

                    @Override
                    public Long getMissedCount() {
                        return row[1] == null
                                ? 0L
                                : ((Number) row[1]).longValue();
                    }

                    @Override
                    public Long getTotalCount() {
                        return row[2] == null
                                ? 0L
                                : ((Number) row[2]).longValue();
                    }

                    @Override
                    public Long getEmployeeCount() {
                        return row[3] == null
                                ? 0L
                                : ((Number) row[3]).longValue();
                    }
                })
                .toList();
    }


    @Override
    public List<TopMissedAreaProjection> getTopMissedAreas() {

        List<Object[]> rows = entityManager.createNativeQuery("""
            SELECT
                ga.area_name AS "areaName",

                COUNT(*) FILTER (
                    WHERE gir.area_correct = FALSE
                ) AS "missedCount",

                COUNT(*) AS "totalCount",

                COUNT(DISTINCT CASE
                    WHEN gir.area_correct = FALSE
                    THEN ases.employee_id
                END) AS "employeeCount"

            FROM assessment.garment_identification_response gir

            JOIN assessment.assessment_response ar
                ON ar.assessment_response_id =
                   gir.assessment_response_id

            JOIN assessment.assessment_module_instance ami
                ON ami.assessment_module_instance_id =
                   ar.assessment_module_instance_id

            JOIN assessment.assessment_attempt aa
                ON aa.assessment_attempt_id =
                   ami.assessment_attempt_id

            JOIN assessment.assessment_session ases
                ON ases.assessment_session_id =
                   aa.assessment_session_id

            JOIN garment.garment_area ga
                ON ga.garment_area_id =
                   gir.selected_garment_area_id

            WHERE gir.area_correct IS NOT NULL

            GROUP BY ga.garment_area_id, ga.area_name

            HAVING COUNT(*) FILTER (
                WHERE gir.area_correct = FALSE
            ) > 0

            ORDER BY
                COUNT(*) FILTER (
                    WHERE gir.area_correct = FALSE
                ) * 100.0 / NULLIF(COUNT(*), 0) DESC,

                COUNT(DISTINCT CASE
                    WHEN gir.area_correct = FALSE
                    THEN ases.employee_id
                END) DESC,

                ga.area_name ASC

            LIMIT 5
            """).getResultList();

        return rows.stream()
                .map(row -> (TopMissedAreaProjection)
                        new TopMissedAreaProjection() {

                            @Override
                            public String getAreaName() {
                                return row[0] == null
                                        ? null : row[0].toString();
                            }

                            @Override
                            public Long getMissedCount() {
                                return row[1] == null
                                        ? 0L
                                        : ((Number) row[1]).longValue();
                            }

                            @Override
                            public Long getTotalCount() {
                                return row[2] == null
                                        ? 0L
                                        : ((Number) row[2]).longValue();
                            }

                            @Override
                            public Long getEmployeeCount() {
                                return row[3] == null
                                        ? 0L
                                        : ((Number) row[3]).longValue();
                            }
                        })
                .toList();
    }

    @Override
    public List<Object[]> getTestDistribution(Long assessmentModuleId) {

        return entityManager.createNativeQuery("""
            SELECT
                cr.competency_rating_id AS category_id,
                cr.rating_name AS category_name,
                COUNT(DISTINCT s.employee_id) AS employee_count
            FROM assessment.assessment_module_instance ami
            JOIN assessment.assessment_attempt aa
                ON aa.assessment_attempt_id = ami.assessment_attempt_id
            JOIN assessment.assessment_session s
                ON s.assessment_session_id = aa.assessment_session_id
            JOIN assessment.assessment_status ast
                ON ast.assessment_status_id = s.assessment_status_id
            JOIN config.competency_rating cr
                ON cr.competency_rating_id = s.competency_rating_id
            WHERE ami.assessment_module_id = :assessmentModuleId
              AND ast.status_code = 'COMPLETED'
              AND s.completed_on IS NOT NULL
              AND s.competency_rating_id IS NOT NULL
            GROUP BY
                cr.competency_rating_id,
                cr.rating_name,
                cr.display_order
            ORDER BY
                cr.display_order NULLS LAST,
                cr.competency_rating_id
            """)
                .setParameter("assessmentModuleId", assessmentModuleId)
                .getResultList();
    }

    public Long countEmployeesAssessedForTest(Long assessmentModuleId) {

        Number result = (Number) entityManager.createNativeQuery("""
            SELECT COUNT(DISTINCT s.employee_id)
            FROM assessment.assessment_module_instance ami
            JOIN assessment.assessment_attempt aa
                ON aa.assessment_attempt_id = ami.assessment_attempt_id
            JOIN assessment.assessment_session s
                ON s.assessment_session_id = aa.assessment_session_id
            JOIN assessment.assessment_status ast
                ON ast.assessment_status_id = s.assessment_status_id
            WHERE ami.assessment_module_id = :assessmentModuleId
              AND ast.status_code = 'COMPLETED'
              AND s.completed_on IS NOT NULL
            """)
                .setParameter("assessmentModuleId", assessmentModuleId)
                .getSingleResult();

        return result.longValue();
    }

}
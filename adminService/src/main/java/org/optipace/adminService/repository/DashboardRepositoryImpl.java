package org.optipace.adminService.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.optipace.adminService.dto.response.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class DashboardRepositoryImpl implements DashboardRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public DashboardSummaryProjection getDashboardSummary(
            Long organisationId,
            Long factoryId) {

        Object[] row = (Object[]) entityManager.createNativeQuery("""
        SELECT
            (
                SELECT COUNT(DISTINCT e.employee_id)
                FROM master.employee e
                INNER JOIN assessment.assessment_session s
                    ON s.employee_id = e.employee_id
                WHERE e.organization_id = :organisationId
                  AND e.factory_id = :factoryId
                  AND e.record_status = 'A'
                  AND s.record_status = 'A'
            ) AS total_employees_assessed,

            (
                SELECT COUNT(DISTINCT s.assessment_session_id)
                FROM assessment.assessment_session s
                INNER JOIN master.employee e
                    ON e.employee_id = s.employee_id
                WHERE e.organization_id = :organisationId
                  AND e.factory_id = :factoryId
                  AND e.record_status = 'A'
                  AND s.record_status = 'A'
                  AND s.assessment_status_id = 3
            ) AS tests_taken,

            (
                SELECT COUNT(DISTINCT s.assessment_session_id)
                FROM assessment.assessment_session s
                INNER JOIN master.employee e
                    ON e.employee_id = s.employee_id
                WHERE e.organization_id = :organisationId
                  AND e.factory_id = :factoryId
                  AND e.record_status = 'A'
                  AND s.record_status = 'A'
                  AND s.assessment_status_id IN (1, 2)
            ) AS tests_pending,

            (
                SELECT COUNT(DISTINCT fs.assessment_session_id)
                FROM assessment.final_score fs
                INNER JOIN assessment.assessment_session s
                    ON s.assessment_session_id =
                       fs.assessment_session_id
                INNER JOIN master.employee e
                    ON e.employee_id = s.employee_id
                WHERE e.organization_id = :organisationId
                  AND e.factory_id = :factoryId
                  AND e.record_status = 'A'
                  AND s.record_status = 'A'
                  AND fs.overall_result = 'FAIL'
            ) AS tests_failed,

            (
                SELECT COUNT(DISTINCT fs.assessment_session_id)
                FROM assessment.final_score fs
                INNER JOIN assessment.assessment_session s
                    ON s.assessment_session_id =
                       fs.assessment_session_id
                INNER JOIN master.employee e
                    ON e.employee_id = s.employee_id
                WHERE e.organization_id = :organisationId
                  AND e.factory_id = :factoryId
                  AND e.record_status = 'A'
                  AND s.record_status = 'A'
                  AND fs.overall_result = 'PASS'
            ) AS good_performance
        """)
                .setParameter("organisationId", organisationId)
                .setParameter("factoryId", factoryId)
                .getSingleResult();

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
            int page,
            int size,
            Long organisationId,
            Long factoryId) {

        int offset = page * size;

        List<Object[]> rows = entityManager.createNativeQuery("""
        WITH ranked_module_scores AS (
            SELECT
                ms.module_score_id,
                ms.assessment_module_instance_id,
                ms.percentage,
                ms.calculated_on,

                ROW_NUMBER() OVER (
                    PARTITION BY ms.assessment_module_instance_id
                    ORDER BY
                        ms.calculated_on DESC NULLS LAST,
                        ms.module_score_id DESC
                ) AS score_rn

            FROM assessment.module_score ms
        )

        SELECT
            e.employee_code,

            CONCAT_WS(
                ' ',
                e.first_name,
                e.middle_name,
                e.last_name
            ) AS employee_name,

            d.department_name,
            am.module_name,

            rms.percentage AS total_score,

            ast.status_name,
            s.completed_on

        FROM assessment.assessment_session s

        INNER JOIN assessment.assessment_status ast
            ON ast.assessment_status_id =
               s.assessment_status_id

        INNER JOIN master.employee e
            ON e.employee_id = s.employee_id

        LEFT JOIN master.department d
            ON d.department_id = e.department_id

        INNER JOIN assessment.assessment_attempt aa
            ON aa.assessment_session_id =
               s.assessment_session_id

        INNER JOIN assessment.assessment_module_instance ami
            ON ami.assessment_attempt_id =
               aa.assessment_attempt_id

        INNER JOIN config.assessment_module am
            ON am.assessment_module_id =
               ami.assessment_module_id

        INNER JOIN ranked_module_scores rms
            ON rms.assessment_module_instance_id =
               ami.assessment_module_instance_id
           AND rms.score_rn = 1

        WHERE ast.status_code = 'COMPLETED'
          AND s.completed_on IS NOT NULL
          AND s.record_status = 'A'
          AND e.record_status = 'A'
          AND ami.is_deleted = FALSE
          AND e.organization_id = :organisationId
          AND e.factory_id = :factoryId

        ORDER BY
            s.completed_on DESC,
            ami.completed_on DESC NULLS LAST,
            s.assessment_session_id DESC

        LIMIT :size OFFSET :offset
        """)
                .setParameter("organisationId", organisationId)
                .setParameter("factoryId", factoryId)
                .setParameter("size", size)
                .setParameter("offset", offset)
                .getResultList();

        return rows.stream()
                .map(row -> new RecentAssessmentResponse(
                        row[0] == null ? null : row[0].toString(),
                        row[1] == null ? null : row[1].toString(),
                        row[2] == null ? null : row[2].toString(),
                        row[3] == null ? null : row[3].toString(),
                        row[4] == null
                                ? null
                                : new java.math.BigDecimal(row[4].toString()),
                        row[5] == null ? null : row[5].toString(),
                        row[6] == null ? null : row[6].toString()
                ))
                .toList();
    }


    @Override
    public long countRecentCompletedAssessments(
            Long organisationId,
            Long factoryId) {

        Number result = (Number) entityManager.createNativeQuery("""
        WITH ranked_module_scores AS (
            SELECT
                ms.module_score_id,
                ms.assessment_module_instance_id,
                ms.calculated_on,

                ROW_NUMBER() OVER (
                    PARTITION BY ms.assessment_module_instance_id
                    ORDER BY
                        ms.calculated_on DESC NULLS LAST,
                        ms.module_score_id DESC
                ) AS score_rn

            FROM assessment.module_score ms
        )

        SELECT COUNT(*)

        FROM assessment.assessment_session s

        INNER JOIN assessment.assessment_status ast
            ON ast.assessment_status_id =
               s.assessment_status_id

        INNER JOIN master.employee e
            ON e.employee_id = s.employee_id

        INNER JOIN assessment.assessment_attempt aa
            ON aa.assessment_session_id =
               s.assessment_session_id

        INNER JOIN assessment.assessment_module_instance ami
            ON ami.assessment_attempt_id =
               aa.assessment_attempt_id

        INNER JOIN ranked_module_scores rms
            ON rms.assessment_module_instance_id =
               ami.assessment_module_instance_id
           AND rms.score_rn = 1

        WHERE ast.status_code = 'COMPLETED'
          AND s.completed_on IS NOT NULL
          AND s.record_status = 'A'
          AND e.record_status = 'A'
          AND ami.is_deleted = FALSE
          AND e.organization_id = :organisationId
          AND e.factory_id = :factoryId
        """)
                .setParameter("organisationId", organisationId)
                .setParameter("factoryId", factoryId)
                .getSingleResult();

        return result.longValue();
    }



    @Override
    public List<CompetencyDistributionProjection> findCompetencyDistribution(
            Long organisationId,
            Long factoryId) {

        List<Object[]> rows = entityManager.createNativeQuery("""
        WITH ranked_final_scores AS (

            -- Step 1: Select the latest final score for each session
            SELECT
                fs.assessment_session_id,
                fs.competency_rating_id,

                ROW_NUMBER() OVER (
                    PARTITION BY fs.assessment_session_id
                    ORDER BY
                        fs.calculated_on DESC,
                        fs.final_score_id DESC
                ) AS score_rn

            FROM assessment.final_score fs

            WHERE fs.competency_rating_id IS NOT NULL
        ),

        ranked_sessions AS (

            -- Step 2: Select the latest eligible session for each employee
            SELECT
                ases.assessment_session_id,
                ases.employee_id,
                rfs.competency_rating_id,

                ROW_NUMBER() OVER (
                    PARTITION BY ases.employee_id
                    ORDER BY
                        ases.completed_on DESC NULLS LAST,
                        ases.created_on DESC,
                        ases.assessment_session_id DESC
                ) AS rn

            FROM assessment.assessment_session ases

            INNER JOIN master.employee e
                ON e.employee_id = ases.employee_id

            INNER JOIN ranked_final_scores rfs
                ON rfs.assessment_session_id = ases.assessment_session_id
               AND rfs.score_rn = 1

            WHERE ases.record_status = 'A'
              AND e.record_status = 'A'
              AND e.organization_id = :organisationId
              AND e.factory_id = :factoryId
        )

        -- Step 3: Group employees by competency rating
        SELECT
            cr.competency_rating_id,
            cr.rating_code,
            cr.rating_name,
            cr.color_code,
            COUNT(rs.employee_id) AS employee_count

        FROM ranked_sessions rs

        INNER JOIN config.competency_rating cr
            ON cr.competency_rating_id = rs.competency_rating_id

        WHERE rs.rn = 1

        GROUP BY
            cr.competency_rating_id,
            cr.rating_code,
            cr.rating_name,
            cr.color_code,
            cr.display_order

        ORDER BY
            cr.display_order NULLS LAST,
            cr.competency_rating_id
        """)
                .setParameter("organisationId", organisationId)
                .setParameter("factoryId", factoryId)
                .getResultList();

        return rows.stream()
                .map(row -> (CompetencyDistributionProjection)
                        new CompetencyDistributionProjection() {

                            @Override
                            public Long getCompetencyRatingId() {
                                return row[0] == null
                                        ? null
                                        : ((Number) row[0]).longValue();
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
                                        ? 0L
                                        : ((Number) row[4]).longValue();
                            }
                        })
                .toList();
    }


    @Override
    public Long countDistinctEmployeesWithCompetencyRating(
            Long organisationId,
            Long factoryId) {

        Number result = (Number) entityManager.createNativeQuery("""
            SELECT COUNT(DISTINCT ases.employee_id)
            FROM assessment.assessment_session ases
            INNER JOIN master.employee e
                ON e.employee_id = ases.employee_id
            INNER JOIN assessment.final_score fs
                ON fs.assessment_session_id = ases.assessment_session_id
            WHERE fs.competency_rating_id IS NOT NULL
              AND e.organization_id = :organisationId
              AND e.factory_id = :factoryId
            """)
                .setParameter("organisationId", organisationId)
                .setParameter("factoryId", factoryId)
                .getSingleResult();

        return result.longValue();
    }



    @Override
    public List<MsaTrendPointResponse> getMsaTrend(
            LocalDate startDate,
            LocalDate endDate,
            Long organisationId,
            Long factoryId) {

        List<Object[]> rows = entityManager.createNativeQuery("""
        WITH months AS (

            -- Step 1: Generate all months between start date and end date
            SELECT generate_series(
                date_trunc('month', CAST(:startDate AS date)),
                date_trunc('month', CAST(:endDate AS date)),
                INTERVAL '1 month'
            ) AS month_start
        ),

        ranked_final_scores AS (

            -- Step 2: Select the latest calculated final score
            -- for each assessment session
            SELECT
                fs.assessment_session_id,
                fs.percentage,

                ROW_NUMBER() OVER (
                    PARTITION BY fs.assessment_session_id
                    ORDER BY
                        fs.calculated_on DESC NULLS LAST,
                        fs.final_score_id DESC
                ) AS score_rn

            FROM assessment.final_score fs
        ),

        monthly_scores AS (

            -- Step 3: Calculate average score for each month
            SELECT
                date_trunc('month', s.completed_on) AS month_start,
                AVG(rfs.percentage) AS average_score

            FROM assessment.assessment_session s

            INNER JOIN assessment.assessment_status ast
                ON ast.assessment_status_id = s.assessment_status_id

            INNER JOIN master.employee e
                ON e.employee_id = s.employee_id

            INNER JOIN ranked_final_scores rfs
                ON rfs.assessment_session_id = s.assessment_session_id
               AND rfs.score_rn = 1

            WHERE ast.status_code = 'COMPLETED'
              AND s.record_status = 'A'
              AND e.record_status = 'A'
              AND s.completed_on IS NOT NULL
              AND rfs.percentage IS NOT NULL

              AND s.completed_on >= CAST(:startDate AS date)
              AND s.completed_on < (
                  CAST(:endDate AS date) + INTERVAL '1 day'
              )

              AND e.organization_id = :organisationId
              AND e.factory_id = :factoryId

            GROUP BY date_trunc('month', s.completed_on)
        )

        -- Step 4: Return all months, including months without assessments
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
                .setParameter("organisationId", organisationId)
                .setParameter("factoryId", factoryId)
                .getResultList();

        return rows.stream()
                .map(row -> new MsaTrendPointResponse(
                        row[0] == null
                                ? null
                                : ((Number) row[0]).intValue(),

                        row[1] == null
                                ? null
                                : ((Number) row[1]).intValue(),

                        row[2] == null
                                ? null
                                : row[2].toString(),

                        row[3] == null
                                ? null
                                : new BigDecimal(row[3].toString())
                                  .setScale(2, RoundingMode.HALF_UP)
                ))
                .toList();
    }

    @Override
    public List<TopMissedDefectProjection> getTopMissedDefects(
            Long organisationId,
            Long factoryId) {

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

            JOIN master.employee e
                ON e.employee_id = ases.employee_id

            JOIN garment.garment_expected_defect ged
                ON ged.garment_expected_defect_id =
                   gir.expected_defect_id

            JOIN garment.defect_master dm
                ON dm.defect_id = ged.defect_id

            WHERE gir.expected_defect_id IS NOT NULL
              AND gir.defect_correct IS NOT NULL
              AND e.organization_id = :organisationId
              AND e.factory_id = :factoryId

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
            """)
                .setParameter("organisationId", organisationId)
                .setParameter("factoryId", factoryId)
                .getResultList();

        return rows.stream()
                .map(row -> (TopMissedDefectProjection)
                        new TopMissedDefectProjection() {

                            @Override
                            public String getDefectName() {
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
    public List<TopMissedAreaProjection> getTopMissedAreas(
            Long organisationId,
            Long factoryId) {

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

            JOIN master.employee e
                ON e.employee_id = ases.employee_id

            JOIN garment.garment_area ga
                ON ga.garment_area_id =
                   gir.selected_garment_area_id

            WHERE gir.area_correct IS NOT NULL
              AND e.organization_id = :organisationId
              AND e.factory_id = :factoryId

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
            """)
                .setParameter("organisationId", organisationId)
                .setParameter("factoryId", factoryId)
                .getResultList();

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
    public List<Object[]> getTestDistribution(
            Long assessmentModuleId,
            Long organisationId,
            Long factoryId) {

        return entityManager.createNativeQuery("""
        WITH ranked_module_scores AS (

            -- Step 1: Get the latest calculated score
            -- for each module instance
            SELECT
                ms.assessment_module_instance_id,
                ms.competency_rating_id,

                ROW_NUMBER() OVER (
                    PARTITION BY ms.assessment_module_instance_id
                    ORDER BY
                        ms.calculated_on DESC NULLS LAST,
                        ms.module_score_id DESC
                ) AS score_rn

            FROM assessment.module_score ms

            WHERE ms.competency_rating_id IS NOT NULL
        ),

        ranked_iq_results AS (

            -- Step 2: Get the latest eligible IQ result
            -- for each employee
            SELECT
                s.employee_id,
                rms.competency_rating_id,

                ROW_NUMBER() OVER (
                    PARTITION BY s.employee_id
                    ORDER BY
                        s.completed_on DESC NULLS LAST,
                        ami.completed_on DESC NULLS LAST,
                        rms.assessment_module_instance_id DESC
                ) AS employee_rn

            FROM assessment.assessment_module_instance ami

            INNER JOIN assessment.assessment_attempt aa
                ON aa.assessment_attempt_id =
                   ami.assessment_attempt_id

            INNER JOIN assessment.assessment_session s
                ON s.assessment_session_id =
                   aa.assessment_session_id

            INNER JOIN assessment.assessment_status ast
                ON ast.assessment_status_id =
                   s.assessment_status_id

            INNER JOIN master.employee e
                ON e.employee_id = s.employee_id

            INNER JOIN ranked_module_scores rms
                ON rms.assessment_module_instance_id =
                   ami.assessment_module_instance_id
               AND rms.score_rn = 1

            WHERE ami.assessment_module_id = :assessmentModuleId
              AND ami.is_deleted = FALSE
              AND ast.status_code = 'COMPLETED'
              AND s.completed_on IS NOT NULL
              AND s.record_status = 'A'
              AND e.record_status = 'A'
              AND e.organization_id = :organisationId
              AND e.factory_id = :factoryId
        )

        -- Step 3: Count employees by IQ competency rating
        SELECT
            cr.competency_rating_id AS category_id,
            cr.rating_name AS category_name,
            COUNT(*) AS employee_count

        FROM ranked_iq_results rir

        INNER JOIN config.competency_rating cr
            ON cr.competency_rating_id = rir.competency_rating_id

        WHERE rir.employee_rn = 1

        GROUP BY
            cr.competency_rating_id,
            cr.rating_name,
            cr.display_order

        ORDER BY
            cr.display_order NULLS LAST,
            cr.competency_rating_id
        """)
                .setParameter("assessmentModuleId", assessmentModuleId)
                .setParameter("organisationId", organisationId)
                .setParameter("factoryId", factoryId)
                .getResultList();
    }




}
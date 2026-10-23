package org.optipace.adminService.repository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.optipace.adminService.dto.response.AssessmentReportResponse;
import org.optipace.adminService.dto.response.EmployeeAssessmentResponse;
import org.optipace.adminService.dto.response.PerformanceSummaryResponse;
import org.optipace.adminService.dto.response.ScoreDistributionResponse;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AssessmentReportRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<AssessmentReportResponse> findAssessmentReportList(
            int page,
            int size,
            String search) {

        String sql = """
        WITH latest_module_instance AS (
            SELECT
                s.assessment_session_id,
                am.assessment_module_id,
                ami.assessment_module_instance_id,
                ami.percentage,
                mst.status_code,
                ROW_NUMBER() OVER (
                    PARTITION BY
                        s.assessment_session_id,
                        am.assessment_module_id
                    ORDER BY
                        ami.created_on DESC,
                        ami.sequence_no DESC
                ) AS rn
            FROM assessment.assessment_session s
            JOIN assessment.assessment_attempt aa
                ON aa.assessment_session_id =
                   s.assessment_session_id
            JOIN assessment.assessment_module_instance ami
                ON ami.assessment_attempt_id =
                   aa.assessment_attempt_id
            JOIN config.assessment_module am
                ON am.assessment_module_id =
                   ami.assessment_module_id
            JOIN assessment.module_status mst
                ON mst.module_status_id =
                   ami.module_status_id
            WHERE ami.is_deleted = FALSE
              AND TRIM(s.record_status) = 'A'
        )
        SELECT
            am.assessment_module_id AS assessment_id,
            am.module_name AS assessment_name,

            COUNT(lmi.assessment_session_id) AS assigned,

            COUNT(lmi.assessment_session_id)
                FILTER (
                    WHERE lmi.status_code = 'COMPLETED'
                ) AS completed,

            0 AS passed,
            0 AS failed,

            AVG(lmi.percentage)
                FILTER (
                    WHERE lmi.status_code = 'COMPLETED'
                ) AS avg_score,

            CASE
                WHEN COUNT(lmi.assessment_session_id) = 0
                    THEN 'NOT_ASSIGNED'

                WHEN COUNT(lmi.assessment_session_id)
                    FILTER (
                        WHERE lmi.status_code = 'COMPLETED'
                    ) = COUNT(lmi.assessment_session_id)
                    THEN 'COMPLETED'

                WHEN COUNT(lmi.assessment_session_id)
                    FILTER (
                        WHERE lmi.status_code = 'IN_PROGRESS'
                    ) > 0
                    THEN 'IN_PROGRESS'

                WHEN COUNT(lmi.assessment_session_id)
                    FILTER (
                        WHERE lmi.status_code = 'AVAILABLE'
                    ) > 0
                    THEN 'AVAILABLE'

                ELSE 'PENDING'
            END AS status

        FROM config.assessment_module am

        LEFT JOIN latest_module_instance lmi
            ON lmi.assessment_module_id =
               am.assessment_module_id
           AND lmi.rn = 1

        WHERE TRIM(am.record_status) = 'A'
          AND (
                CAST(:search AS VARCHAR) IS NULL
                OR am.module_name ILIKE
                   CONCAT('%', CAST(:search AS VARCHAR), '%')
              )

        GROUP BY
            am.assessment_module_id,
            am.module_name,
            am.display_order

        ORDER BY
            am.display_order NULLS LAST,
            am.module_name

        LIMIT :size OFFSET :offset
        """;

        List<Object[]> rows = entityManager
                .createNativeQuery(sql)
                .setParameter(
                        "search",
                        search == null || search.isBlank()
                                ? null
                                : search.trim()
                )
                .setParameter("size", size)
                .setParameter("offset", (long) (page - 1) * size)
                .getResultList();

        List<AssessmentReportResponse> reports = new ArrayList<>();

        for (Object[] row : rows) {

            AssessmentReportResponse dto =
                    new AssessmentReportResponse();

            // 0: assessment_module_id
            dto.setAssessmentId(
                    row[0] != null
                            ? ((Number) row[0]).intValue()
                            : null
            );

            // 1: assessment_name
            dto.setAssessmentName(
                    row[1] != null ? row[1].toString() : null
            );

            // 2: assigned
            dto.setAssigned(((Number) row[2]).intValue());

            // 3: completed
            dto.setCompleted(((Number) row[3]).intValue());

            // 4: passed
            dto.setPassed(((Number) row[4]).intValue());

            // 5: failed
            dto.setFailed(((Number) row[5]).intValue());

            // 6: avg_score
            BigDecimal avgScore = row[6] != null
                    ? new BigDecimal(row[6].toString())
                    : BigDecimal.ZERO;

            dto.setAvgScore(
                    avgScore.setScale(2, RoundingMode.HALF_UP)
                            .toPlainString()
            );

            // 7: status
            dto.setStatus(
                    row[7] != null ? row[7].toString() : null
            );

            reports.add(dto);
        }

        return reports;
    }

    public PerformanceSummaryResponse getPerformanceSummary(
            Long assessmentId) {

        String sql = """
        WITH latest AS (
            SELECT
                s.assessment_session_id,
                mst.status_code,
                ms.percentage,
                fs.overall_result,
                ROW_NUMBER() OVER (
                    PARTITION BY s.assessment_session_id
                    ORDER BY ami.created_on DESC,
                             ami.sequence_no DESC
                ) AS rn
            FROM assessment.assessment_session s
            JOIN assessment.assessment_attempt aa
                ON aa.assessment_session_id =
                   s.assessment_session_id
            JOIN assessment.assessment_module_instance ami
                ON ami.assessment_attempt_id =
                   aa.assessment_attempt_id
            JOIN assessment.module_status mst
                ON mst.module_status_id = ami.module_status_id
            LEFT JOIN assessment.module_score ms
                ON ms.assessment_module_instance_id =
                   ami.assessment_module_instance_id
            LEFT JOIN assessment.final_score fs
                ON fs.assessment_session_id =
                   s.assessment_session_id
            WHERE ami.assessment_module_id = :assessmentId
              AND ami.is_deleted = FALSE
              AND TRIM(s.record_status) = 'A'
        )
        SELECT
            COALESCE(AVG(percentage)
                FILTER (WHERE status_code = 'COMPLETED'), 0),
            COUNT(*),
            COUNT(*) FILTER (WHERE status_code = 'COMPLETED'),
            COUNT(*) FILTER (
                WHERE LOWER(TRIM(overall_result)) IN ('pass', 'passed')
            ),
            COUNT(*) FILTER (
                WHERE LOWER(TRIM(overall_result)) IN ('fail', 'failed')
            )
        FROM latest
        WHERE rn = 1
        """;

        Object[] row = (Object[]) entityManager
                .createNativeQuery(sql)
                .setParameter("assessmentId", assessmentId)
                .getSingleResult();

        PerformanceSummaryResponse response =
                new PerformanceSummaryResponse();

        response.setAverageScore(
                row[0] == null
                        ? BigDecimal.ZERO
                        : new BigDecimal(row[0].toString())
                          .setScale(2, RoundingMode.HALF_UP));

        response.setTotalAssessments(((Number) row[1]).intValue());
        response.setCompletedAssessments(((Number) row[2]).intValue());
        response.setPassedAssessments(((Number) row[3]).intValue());
        response.setFailedAssessments(((Number) row[4]).intValue());

        return response;
    }

    public List<ScoreDistributionResponse> getScoreDistribution(
            Long assessmentId) {

        String sql = """
        WITH latest AS (
            SELECT
                s.assessment_session_id,
                ms.percentage,
                mst.status_code,
                ROW_NUMBER() OVER (
                    PARTITION BY s.assessment_session_id
                    ORDER BY ami.created_on DESC,
                             ami.sequence_no DESC
                ) AS rn
            FROM assessment.assessment_session s
            JOIN assessment.assessment_attempt aa
                ON aa.assessment_session_id =
                   s.assessment_session_id
            JOIN assessment.assessment_module_instance ami
                ON ami.assessment_attempt_id =
                   aa.assessment_attempt_id
            JOIN assessment.module_status mst
                ON mst.module_status_id = ami.module_status_id
            LEFT JOIN assessment.module_score ms
                ON ms.assessment_module_instance_id =
                   ami.assessment_module_instance_id
            WHERE ami.assessment_module_id = :assessmentId
              AND ami.is_deleted = FALSE
              AND TRIM(s.record_status) = 'A'
        )
        SELECT
            CASE
                WHEN percentage < 20 THEN '0-19%'
                WHEN percentage < 40 THEN '20-39%'
                WHEN percentage < 60 THEN '40-59%'
                WHEN percentage < 80 THEN '60-79%'
                ELSE '80-100%'
            END AS score_range,
            COUNT(*) AS employee_count
        FROM latest
        WHERE rn = 1
          AND status_code = 'COMPLETED'
          AND percentage IS NOT NULL
        GROUP BY
            CASE
                WHEN percentage < 20 THEN '0-19%'
                WHEN percentage < 40 THEN '20-39%'
                WHEN percentage < 60 THEN '40-59%'
                WHEN percentage < 80 THEN '60-79%'
                ELSE '80-100%'
            END
        ORDER BY MIN(percentage)
        """;

        List<Object[]> rows = entityManager
                .createNativeQuery(sql)
                .setParameter("assessmentId", assessmentId)
                .getResultList();

        List<ScoreDistributionResponse> result = new ArrayList<>();

        for (Object[] row : rows) {
            result.add(ScoreDistributionResponse.builder()
                    .scoreRange(row[0].toString())
                    .employeeCount(((Number) row[1]).intValue())
                    .build());
        }

        return result;
    }

    public List<EmployeeAssessmentResponse> getEmployeeResults(
            Long assessmentId,
            int page,
            int size,
            String search) {

        String sql = """
        WITH latest AS (
            SELECT
                s.assessment_session_id,
                s.employee_id,
                ms.percentage,
                fs.overall_result,
                mst.status_code,
                ROW_NUMBER() OVER (
                    PARTITION BY s.employee_id
                    ORDER BY s.created_on DESC,
                             ami.created_on DESC,
                             ami.sequence_no DESC
                ) AS rn
            FROM assessment.assessment_session s
            JOIN assessment.assessment_attempt aa
                ON aa.assessment_session_id =
                   s.assessment_session_id
            JOIN assessment.assessment_module_instance ami
                ON ami.assessment_attempt_id =
                   aa.assessment_attempt_id
            JOIN assessment.module_status mst
                ON mst.module_status_id = ami.module_status_id
            LEFT JOIN assessment.module_score ms
                ON ms.assessment_module_instance_id =
                   ami.assessment_module_instance_id
            LEFT JOIN assessment.final_score fs
                ON fs.assessment_session_id =
                   s.assessment_session_id
            WHERE ami.assessment_module_id = :assessmentId
              AND ami.is_deleted = FALSE
              AND TRIM(s.record_status) = 'A'
        )
        SELECT
            CONCAT_WS(
                ' ',
                e.first_name,
                NULLIF(TRIM(e.middle_name), ''),
                e.last_name
            ) AS employee_name,
            e.employee_code AS employee_id,
            d.department_name AS department,
            COALESCE(l.percentage, 0) AS score,
            COALESCE(
                l.overall_result,
                l.status_code
            ) AS result
        FROM latest l
        JOIN master.employee e
            ON e.employee_id = l.employee_id
        LEFT JOIN master.department d
            ON d.department_id = e.department_id
        WHERE l.rn = 1
          AND (
              CAST(:search AS VARCHAR) IS NULL
              OR CONCAT_WS(
                  ' ',
                  e.first_name,
                  NULLIF(TRIM(e.middle_name), ''),
                  e.last_name
              ) ILIKE CONCAT('%', CAST(:search AS VARCHAR), '%')
              OR e.employee_code ILIKE
                  CONCAT('%', CAST(:search AS VARCHAR), '%')
          )
        ORDER BY employee_name
        LIMIT :size OFFSET :offset
        """;

        List<Object[]> rows = entityManager
                .createNativeQuery(sql)
                .setParameter("assessmentId", assessmentId)
                .setParameter("search", search)
                .setParameter("size", size)
                .setParameter("offset", (long) (page - 1) * size)
                .getResultList();

        List<EmployeeAssessmentResponse> result = new ArrayList<>();

        for (Object[] row : rows) {
            result.add(EmployeeAssessmentResponse.builder()
                    .employeeName(
                            row[0] != null ? row[0].toString() : null)
                    .employeeId(
                            row[1] != null ? row[1].toString() : null)
                    .department(
                            row[2] != null ? row[2].toString() : null)
                    .score(
                            row[3] != null
                                    ? ((Number) row[3]).doubleValue()
                                    : 0.0)
                    .result(
                            row[4] != null ? row[4].toString() : null)
                    .build());
        }

        return result;
    }
}
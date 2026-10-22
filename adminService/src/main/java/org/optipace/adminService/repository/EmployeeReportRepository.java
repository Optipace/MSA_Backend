package org.optipace.adminService.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.optipace.adminService.dto.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class EmployeeReportRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private static final String REPORT_QUERY = """
        SELECT
            e.employee_code AS employee_id,
            CONCAT_WS(' ',
                NULLIF(TRIM(e.first_name), ''),
                NULLIF(TRIM(e.middle_name), ''),
                NULLIF(TRIM(e.last_name), '')
            ) AS employee_name,
            d.department_name AS department,
            f.factory_name AS factory,
            COALESCE(mc.completed_count, 0) AS completed_assessments,
            COALESCE(mc.total_count, 0) AS total_assessments,
            fs.percentage AS overall_score,
            fs.overall_result AS result

        FROM master.employee e

        JOIN master.department d
            ON d.department_id = e.department_id

        JOIN master.factory f
            ON f.factory_id = e.factory_id

        LEFT JOIN LATERAL (
            SELECT s.assessment_session_id
            FROM assessment.assessment_session s
            JOIN assessment.assessment_status ast
                ON ast.assessment_status_id = s.assessment_status_id
            WHERE s.employee_id = e.employee_id
              AND TRIM(s.record_status) = 'A'
              AND ast.status_code = 'COMPLETED'
            ORDER BY s.completed_on DESC NULLS LAST,
                     s.created_on DESC
            LIMIT 1
        ) latest ON TRUE

        LEFT JOIN assessment.final_score fs
            ON fs.assessment_session_id = latest.assessment_session_id

        LEFT JOIN LATERAL (
            SELECT
                COUNT(DISTINCT ami.assessment_module_id)
                    FILTER (
                        WHERE ms.status_code = 'COMPLETED'
                          AND ami.is_deleted = FALSE
                    ) AS completed_count,
                (
                    SELECT COUNT(*)
                    FROM config.assessment_module am
                    WHERE TRIM(am.record_status) = 'A'
                ) AS total_count
            FROM assessment.assessment_module_instance ami
            JOIN assessment.assessment_attempt aa
                ON aa.assessment_attempt_id = ami.assessment_attempt_id
            JOIN assessment.module_status ms
                ON ms.module_status_id = ami.module_status_id
            WHERE aa.assessment_session_id =
                  latest.assessment_session_id
        ) mc ON TRUE

        WHERE TRIM(e.record_status) = 'A'
          AND (
              CAST(:search AS VARCHAR) IS NULL
              OR CONCAT_WS(' ',
                  e.first_name,
                  e.middle_name,
                  e.last_name
              ) ILIKE CONCAT('%', CAST(:search AS VARCHAR), '%')
          )
        ORDER BY e.employee_id DESC
        """;

    private static final String COUNT_QUERY = """
    SELECT COUNT(*)
    FROM master.employee e
    WHERE TRIM(e.record_status) = 'A'
      AND (
          CAST(:search AS VARCHAR) IS NULL
          OR CONCAT_WS(' ',
              e.first_name,
              e.middle_name,
              e.last_name
          ) ILIKE CONCAT('%', CAST(:search AS VARCHAR), '%')
      )
    """;

    public Page<EmployeeReportProjection> findEmployeeReport(
            String search, Pageable pageable) {

        Query query = entityManager.createNativeQuery(REPORT_QUERY);
        query.setParameter("search", search);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.getResultList();

        List<EmployeeReportProjection> results = rows.stream()
                .map(row -> new EmployeeReportProjection(
                        (String) row[0],
                        (String) row[1],
                        (String) row[2],
                        (String) row[3],
                        ((Number) row[4]).longValue(),
                        ((Number) row[5]).longValue(),
                        (BigDecimal) row[6],
                        (String) row[7]
                ))
                .toList();

        Query countQuery = entityManager.createNativeQuery(COUNT_QUERY);
        countQuery.setParameter("search", search);

        long total = ((Number) countQuery.getSingleResult()).longValue();

        return new PageImpl<>(results, pageable, total);
    }

    public EmployeeDetailsResponse findEmployeeDetails(String employeeId) {

        // 1. Fetch employee details and latest completed assessment
        String employeeQuery = """
        SELECT
            e.employee_code,
            CONCAT_WS(' ',
                NULLIF(TRIM(e.first_name), ''),
                NULLIF(TRIM(e.middle_name), ''),
                NULLIF(TRIM(e.last_name), '')
            ) AS employee_name,
            d.department_name,
            f.factory_name,
            s.assessment_session_id,
            fs.percentage AS overall_score,
            fs.overall_result
        FROM master.employee e
        LEFT JOIN master.department d
            ON d.department_id = e.department_id
        LEFT JOIN master.factory f
            ON f.factory_id = e.factory_id
        LEFT JOIN LATERAL (
            SELECT s1.*
            FROM assessment.assessment_session s1
            JOIN assessment.assessment_status ast
                ON ast.assessment_status_id = s1.assessment_status_id
            WHERE s1.employee_id = e.employee_id
              AND TRIM(s1.record_status) = 'A'
              AND ast.status_code = 'COMPLETED'
            ORDER BY
                s1.completed_on DESC NULLS LAST,
                s1.created_on DESC
            LIMIT 1
        ) s ON TRUE
        LEFT JOIN assessment.final_score fs
            ON fs.assessment_session_id = s.assessment_session_id
        WHERE TRIM(e.record_status) = 'A'
          AND e.employee_code = :employeeId
        """;

        List<Object[]> employeeRows = entityManager
                .createNativeQuery(employeeQuery)
                .setParameter("employeeId", employeeId)
                .getResultList();

        if (employeeRows.isEmpty()) {
            return null;
        }

        Object[] employee = employeeRows.get(0);

        EmployeeDetailsResponse response = new EmployeeDetailsResponse();

        response.setEmployeeId(employee[0] != null
                ? employee[0].toString()
                : null);

        response.setEmployeeName(employee[1] != null
                ? employee[1].toString().trim()
                : null);

        response.setDepartment(employee[2] != null
                ? employee[2].toString()
                : null);

        response.setFactory(employee[3] != null
                ? employee[3].toString()
                : null);

        BigDecimal overallScore = employee[5] != null
                ? new BigDecimal(employee[5].toString())
                : null;

        String overallResult = employee[6] != null
                ? employee[6].toString()
                : null;

        // 2. Initialize response lists
        List<AssessmentPerformanceResponse> performances =
                new ArrayList<>();

        List<AssessmentResultResponse> assessmentResults =
                new ArrayList<>();

        PerformanceSummaryResponse summary =
                new PerformanceSummaryResponse();

        summary.setAverageScore(overallScore);

        // 3. Load module performance only when a completed session exists
        int completedCount = 0;

        if (employee[4] != null) {

            Object sessionId = employee[4];

            String moduleQuery = """
            SELECT
                am.module_code,
                am.module_name,
                ami.assessment_module_instance_id,
                ami.obtained_score,
                ami.maximum_score,
                ami.percentage,
                ami.completed_on,
                mst.status_code
            FROM assessment.assessment_session s
            JOIN assessment.assessment_attempt aa
                ON aa.assessment_session_id = s.assessment_session_id
            JOIN assessment.assessment_module_instance ami
                ON ami.assessment_attempt_id = aa.assessment_attempt_id
            JOIN config.assessment_module am
                ON am.assessment_module_id = ami.assessment_module_id
            JOIN assessment.module_status mst
                ON mst.module_status_id = ami.module_status_id
            WHERE s.assessment_session_id = :sessionId
              AND ami.is_deleted = FALSE
            ORDER BY am.display_order
            """;

            List<Object[]> moduleRows = entityManager
                    .createNativeQuery(moduleQuery)
                    .setParameter("sessionId", sessionId)
                    .getResultList();

            for (Object[] row : moduleRows) {

                String moduleCode = row[0] != null
                        ? row[0].toString()
                        : null;

                String moduleName = row[1] != null
                        ? row[1].toString()
                        : null;

                BigDecimal obtainedScore = row[3] != null
                        ? new BigDecimal(row[3].toString())
                        : null;

                BigDecimal maximumScore = row[4] != null
                        ? new BigDecimal(row[4].toString())
                        : null;

                BigDecimal percentage = row[5] != null
                        ? new BigDecimal(row[5].toString())
                        : null;

                OffsetDateTime completedOn = row[6] instanceof OffsetDateTime
                        ? (OffsetDateTime) row[6]
                        : null;

                String moduleStatus = row[7] != null
                        ? row[7].toString()
                        : null;

                // Module performance
                AssessmentPerformanceResponse performance =
                        new AssessmentPerformanceResponse();

                performance.setModuleCode(moduleCode);
                performance.setAssessmentName(moduleName);
                performance.setScore(percentage);

                performances.add(performance);

                // Count completed modules
                if ("COMPLETED".equalsIgnoreCase(moduleStatus)) {
                    completedCount++;
                }

                // Assessment/module result history
                AssessmentResultResponse result =
                        new AssessmentResultResponse();

                result.setAssessmentName(moduleName);
                result.setScore(percentage);

                if (completedOn != null) {
                    result.setDate(completedOn.toLocalDate());
                }

                /*
                 * Do not assign PASS/FAIL here unless your database
                 * stores a module-level result or you have a defined
                 * scoring rule for each module.
                 */
                result.setResult(null);

                assessmentResults.add(result);
            }
        }

        // 4. Get total active assessment modules
        String totalModulesQuery = """
        SELECT COUNT(*)
        FROM config.assessment_module
        WHERE TRIM(record_status) = 'A'
        """;

        Number totalModules = (Number) entityManager
                .createNativeQuery(totalModulesQuery)
                .getSingleResult();

        // 5. Populate performance summary
        summary.setCompletedAssessments(completedCount);
        summary.setTotalAssessments(totalModules.intValue());

        /*
         * These are placeholders because individual module PASS/FAIL
         * rules have not been established in the current implementation.
         */
        summary.setPassedAssessments(0);
        summary.setFailedAssessments(0);

        // If you add overallResult to PerformanceSummaryResponse:
        // summary.setOverallResult(overallResult);

        response.setPerformanceSummary(summary);
        response.setAssessmentPerformance(performances);
        response.setAssessmentResults(assessmentResults);

        return response;
    }
}
package org.optipace.adminService.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AssessorReportViewRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Find the latest assessment session using the unique employee code.
     */
    public UUID getLatestSessionIdByEmpCode(String empCode) {

        String sql = """
            SELECT s.assessment_session_id
            FROM master.employee e
            JOIN assessment.assessment_session s
                ON s.employee_id = e.employee_id
            WHERE e.employee_code = ?
              AND e.record_status = 'A'
            ORDER BY
                s.assigned_on DESC NULLS LAST,
                s.assessment_session_id DESC
            LIMIT 1
            """;

        List<UUID> sessionIds = jdbcTemplate.query(
                sql,
                (rs, rowNum) ->
                        rs.getObject("assessment_session_id", UUID.class),
                empCode
        );

        if (sessionIds.isEmpty()) {
            throw new IllegalArgumentException(
                    "No assessment session found for employee code: "
                            + empCode);
        }

        return sessionIds.get(0);
    }

    /**
     * Fetch employee summary and assessment statistics.
     */
    public Map<String, Object> getAssessmentSummaryBySessionId(
            UUID sessionId) {

        String sql = """
            SELECT
                CONCAT_WS(
                    ' ',
                    NULLIF(TRIM(e.first_name), ''),
                    NULLIF(TRIM(e.middle_name), ''),
                    NULLIF(TRIM(e.last_name), '')
                ) AS "employeeName",

                e.employee_code AS "employeeCode",
                d.department_name AS "departmentName",
                f.factory_name AS "factoryName",

                ROUND(
                    COALESCE(fs.percentage, s.percentage, 0)::numeric,
                    0
                ) AS "overallScore",

                COUNT(DISTINCT ami.assessment_module_instance_id)
                    AS "totalModules",

                COUNT(DISTINCT ami.assessment_module_instance_id)
                    FILTER (
                        WHERE ms.status_code = 'COMPLETED'
                    ) AS "completedModules",

                COUNT(DISTINCT ami.assessment_module_instance_id)
                    FILTER (
                        WHERE ms.status_code = 'COMPLETED'
                          AND ami.percentage >= t.pass_percentage
                    ) AS "passedCount",

                COUNT(DISTINCT ami.assessment_module_instance_id)
                    FILTER (
                        WHERE ms.status_code = 'COMPLETED'
                          AND ami.percentage < t.pass_percentage
                    ) AS "failedCount"

            FROM assessment.assessment_session s

            JOIN master.employee e
                ON s.employee_id = e.employee_id

            LEFT JOIN master.department d
                ON e.department_id = d.department_id

            LEFT JOIN master.factory f
                ON e.factory_id = f.factory_id

            LEFT JOIN config.assessment_template t
                ON s.assessment_template_id =
                   t.assessment_template_id

            LEFT JOIN assessment.assessment_attempt aa
                ON s.assessment_session_id =
                   aa.assessment_session_id

            LEFT JOIN assessment.assessment_module_instance ami
                ON aa.assessment_attempt_id =
                   ami.assessment_attempt_id

            LEFT JOIN assessment.module_status ms
                ON ami.module_status_id =
                   ms.module_status_id

            LEFT JOIN assessment.final_score fs
                ON s.assessment_session_id =
                   fs.assessment_session_id

            WHERE s.assessment_session_id = ?

            GROUP BY
                e.employee_code,
                e.first_name,
                e.middle_name,
                e.last_name,
                d.department_name,
                f.factory_name,
                fs.percentage,
                s.percentage,
                t.pass_percentage
            """;

        return jdbcTemplate.queryForMap(sql, sessionId);
    }

    /**
     * Fetch the test-by-test breakdown for an assessment session.
     */
    public List<Map<String, Object>> getAssessmentDetailsBySessionId(
            UUID sessionId) {

        String sql = """
            SELECT
                ami.assessment_module_id AS "moduleId",
                am.module_name AS "testName",
                ami.started_on::text AS "testDate",

                ROUND(
                    COALESCE(ami.obtained_score, 0)::numeric,
                    0
                ) AS "obtainedScore",

                ROUND(
                    COALESCE(ami.maximum_score, 0)::numeric,
                    0
                ) AS "totalScore",

                ROUND(
                    COALESCE(ami.percentage, 0)::numeric,
                    0
                ) AS "percentage",

                COALESCE(ms.status_code, 'PENDING')
                    AS "moduleStatus",

                CASE
                    WHEN ms.status_code = 'COMPLETED'
                         AND ami.percentage >= t.pass_percentage
                        THEN 'Passed'

                    WHEN ms.status_code = 'COMPLETED'
                        THEN 'Failed'

                    ELSE 'Pending'
                END AS "result"

            FROM assessment.assessment_session s

            JOIN config.assessment_template t
                ON s.assessment_template_id =
                   t.assessment_template_id

            JOIN assessment.assessment_attempt aa
                ON s.assessment_session_id =
                   aa.assessment_session_id

            JOIN assessment.assessment_module_instance ami
                ON aa.assessment_attempt_id =
                   ami.assessment_attempt_id

            JOIN config.assessment_module am
                ON ami.assessment_module_id =
                   am.assessment_module_id

            LEFT JOIN assessment.module_status ms
                ON ami.module_status_id =
                   ms.module_status_id

            WHERE s.assessment_session_id = ?

            ORDER BY am.display_order
            """;

        return jdbcTemplate.queryForList(sql, sessionId);
    }
}

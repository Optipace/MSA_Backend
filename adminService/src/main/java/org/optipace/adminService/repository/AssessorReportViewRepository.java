
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
     * Find the latest assessment session using employee code.
     * Returns null when no assessment session exists.
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

        return sessionIds.isEmpty() ? null : sessionIds.get(0);
    }

    /**
     * Check whether an active employee exists.
     */
    public boolean employeeExists(String empCode) {

        String sql = """
            SELECT EXISTS (
                SELECT 1
                FROM master.employee
                WHERE employee_code = ?
                  AND record_status = 'A'
            )
            """;

        Boolean exists = jdbcTemplate.queryForObject(
                sql,
                Boolean.class,
                empCode
        );

        return Boolean.TRUE.equals(exists);
    }

    /**
     * Fetch employee summary using the latest result
     * of each assessment module.
     */
    public Map<String, Object> getAssessmentSummaryBySessionId(
            UUID sessionId) {

        String sql = """
            WITH ranked_modules AS (
                SELECT
                    ami.*,
                    ms.status_code,
                    ROW_NUMBER() OVER (
                        PARTITION BY ami.assessment_module_id
                        ORDER BY
                            ami.started_on DESC NULLS LAST,
                            ami.assessment_module_instance_id DESC
                    ) AS rn
                FROM assessment.assessment_attempt aa
                JOIN assessment.assessment_module_instance ami
                    ON aa.assessment_attempt_id =
                       ami.assessment_attempt_id
                LEFT JOIN assessment.module_status ms
                    ON ami.module_status_id =
                       ms.module_status_id
                WHERE aa.assessment_session_id = ?
            ),
            latest_modules AS (
                SELECT *
                FROM ranked_modules
                WHERE rn = 1
            )
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

                COUNT(lm.assessment_module_instance_id)
                    AS "totalModules",

                COUNT(lm.assessment_module_instance_id)
                    FILTER (
                        WHERE lm.status_code = 'COMPLETED'
                    ) AS "completedModules",

                COUNT(lm.assessment_module_instance_id)
                    FILTER (
                        WHERE lm.status_code = 'COMPLETED'
                          AND lm.percentage >= t.pass_percentage
                    ) AS "passedCount",

                COUNT(lm.assessment_module_instance_id)
                    FILTER (
                        WHERE lm.status_code = 'COMPLETED'
                          AND lm.percentage < t.pass_percentage
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

            LEFT JOIN latest_modules lm
                ON TRUE

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

        return jdbcTemplate.queryForMap(sql, sessionId, sessionId);
    }

    /**
     * Fetch only the latest test result for each module.
     */
    public List<Map<String, Object>> getAssessmentDetailsBySessionId(
            UUID sessionId) {

        String sql = """
            WITH ranked_modules AS (
                SELECT
                    ami.*,
                    ms.status_code,
                    ROW_NUMBER() OVER (
                        PARTITION BY ami.assessment_module_id
                        ORDER BY
                            ami.started_on DESC NULLS LAST,
                            ami.assessment_module_instance_id DESC
                    ) AS rn
                FROM assessment.assessment_attempt aa
                JOIN assessment.assessment_module_instance ami
                    ON aa.assessment_attempt_id =
                       ami.assessment_attempt_id
                LEFT JOIN assessment.module_status ms
                    ON ami.module_status_id =
                       ms.module_status_id
                WHERE aa.assessment_session_id = ?
            )
            SELECT
                lm.assessment_module_id AS "moduleId",

                am.module_name AS "testName",

                lm.started_on::text AS "testDate",

                ROUND(
                    COALESCE(lm.obtained_score, 0)::numeric,
                    0
                ) AS "obtainedScore",

                ROUND(
                    COALESCE(lm.maximum_score, 0)::numeric,
                    0
                ) AS "totalScore",

                ROUND(
                    COALESCE(lm.percentage, 0)::numeric,
                    0
                ) AS "percentage",

                COALESCE(lm.status_code, 'PENDING')
                    AS "moduleStatus",

                CASE
                    WHEN lm.status_code = 'COMPLETED'
                         AND lm.percentage >= t.pass_percentage
                        THEN 'Passed'

                    WHEN lm.status_code = 'COMPLETED'
                        THEN 'Failed'

                    ELSE 'Pending'
                END AS "result"

            FROM ranked_modules lm

            JOIN config.assessment_module am
                ON lm.assessment_module_id =
                   am.assessment_module_id

            JOIN assessment.assessment_attempt aa
                ON lm.assessment_attempt_id =
                   aa.assessment_attempt_id

            JOIN assessment.assessment_session s
                ON aa.assessment_session_id =
                   s.assessment_session_id

            JOIN config.assessment_template t
                ON s.assessment_template_id =
                   t.assessment_template_id

            WHERE lm.rn = 1

            ORDER BY am.display_order
            """;

        return jdbcTemplate.queryForList(sql, sessionId);
    }
}

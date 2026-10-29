package org.optipace.adminService.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AssessorReportRepository {

    private final JdbcTemplate jdbcTemplate;

    public Map<String, Object> getAssessorReport(
            Long organizationId,
            Long factoryId,
            int page,
            int size) {

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page number cannot be negative");
        }

        if (size <= 0) {
            throw new IllegalArgumentException(
                    "Page size must be greater than zero");
        }

        if (organizationId == null) {
            throw new IllegalArgumentException(
                    "Organization ID is required");
        }

        int offset = Math.multiplyExact(page, size);

        String sql = """
            WITH dashboard_employees AS (
                SELECT
                    e.employee_id,
                    e.employee_code,
                    e.first_name,
                    e.middle_name,
                    e.last_name,
                    e.department_id,
                    e.factory_id
                FROM master.employee e
                WHERE e.record_status = 'A'
                  AND e.organization_id = ?
                  AND (? IS NULL OR e.factory_id = ?)
            ),

            latest_session AS (
                SELECT
                    e.employee_id,
                    s.assessment_session_id,
                    s.assessment_template_id,
                    s.assigned_on,
                    s.percentage,
                    s.assessment_status_id
                FROM dashboard_employees e

                LEFT JOIN LATERAL (
                    SELECT
                        s1.assessment_session_id,
                        s1.assessment_template_id,
                        s1.assigned_on,
                        s1.percentage,
                        s1.assessment_status_id
                    FROM assessment.assessment_session s1
                    WHERE s1.employee_id = e.employee_id
                    ORDER BY
                        s1.assigned_on DESC NULLS LAST,
                        s1.assessment_session_id DESC
                    LIMIT 1
                ) s ON TRUE
            ),

            report_data AS (
                SELECT
                    s.assessment_session_id AS "sessionId",
                    e.employee_id AS "employeeId",
                    e.employee_code AS "empCode",

                    CONCAT_WS(
                        ' ',
                        NULLIF(TRIM(e.first_name), ''),
                        NULLIF(TRIM(e.middle_name), ''),
                        NULLIF(TRIM(e.last_name), '')
                    ) AS employee,

                    d.department_name AS department,
                    f.factory_name AS factory,

                    CASE
                        WHEN s.assessment_session_id IS NULL
                            THEN '0/0'
                        ELSE CONCAT(
                            COUNT(*) FILTER (
                                WHERE ms.status_code = 'COMPLETED'
                            ),
                            '/',
                            COUNT(
                                ami.assessment_module_instance_id
                            )
                        )
                    END AS assessment,

                    CASE
                        WHEN s.assessment_session_id IS NULL
                            THEN '0%'
                        ELSE CONCAT(
                            ROUND(
                                COALESCE(
                                    fs.percentage,
                                    s.percentage,
                                    0
                                )::numeric,
                                0
                            ),
                            '%'
                        )
                    END AS "overallScore",

                    COALESCE(
                        fs.overall_result,
                        aa.result,
                        'N/A'
                    ) AS result,

                    s.assigned_on AS "assignedOn"

                FROM latest_session s

                JOIN dashboard_employees e
                    ON e.employee_id = s.employee_id

                LEFT JOIN master.department d
                    ON d.department_id = e.department_id

                LEFT JOIN master.factory f
                    ON f.factory_id = e.factory_id

                LEFT JOIN assessment.assessment_attempt aa
                    ON aa.assessment_session_id =
                       s.assessment_session_id

                LEFT JOIN assessment.assessment_module_instance ami
                    ON ami.assessment_attempt_id =
                       aa.assessment_attempt_id

                LEFT JOIN assessment.module_status ms
                    ON ms.module_status_id =
                       ami.module_status_id

                LEFT JOIN assessment.final_score fs
                    ON fs.assessment_session_id =
                       s.assessment_session_id

                GROUP BY
                    s.assessment_session_id,
                    e.employee_id,
                    e.employee_code,
                    e.first_name,
                    e.middle_name,
                    e.last_name,
                    d.department_name,
                    f.factory_name,
                    fs.percentage,
                    s.percentage,
                    fs.overall_result,
                    aa.result,
                    s.assigned_on
            )

            SELECT
                report_data.*,
                COUNT(*) OVER() AS "totalElements"
            FROM report_data
            ORDER BY
                "assignedOn" DESC NULLS LAST,
                "employeeId"
            LIMIT ? OFFSET ?
            """;

        log.debug(
                "Fetching assessor report: organizationId={}, " +
                "factoryId={}, page={}, size={}",
                organizationId,
                factoryId,
                page,
                size
        );

        List<Map<String, Object>> rows = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {
                    Map<String, Object> report =
                            new LinkedHashMap<>();

                    report.put("sessionId",
                            rs.getObject("sessionId"));

                    report.put("employeeId",
                            rs.getLong("employeeId"));

                    report.put("empCode",
                            rs.getString("empCode"));

                    report.put("employee",
                            rs.getString("employee"));

                    report.put("department",
                            rs.getString("department"));

                    report.put("factory",
                            rs.getString("factory"));

                    report.put("assessment",
                            rs.getString("assessment"));

                    report.put("overallScore",
                            rs.getString("overallScore"));

                    report.put("result",
                            rs.getString("result"));

                    report.put("totalElements",
                            rs.getLong("totalElements"));

                    return report;
                },
                organizationId,
                factoryId,
                factoryId,
                size,
                offset
        );

        /*
         * Extract totalElements from the window count.
         */
        long totalElements = rows.isEmpty()
                ? 0L
                : ((Number) rows.get(0).get("totalElements"))
                        .longValue();

        /*
         * Remove internal pagination count from report content.
         */
        List<Map<String, Object>> content = new ArrayList<>();

        for (Map<String, Object> row : rows) {
            Map<String, Object> report =
                    new LinkedHashMap<>(row);

            report.remove("totalElements");
            content.add(report);
        }

        /*
         * Pagination metadata.
         */
        int totalPages = (int) Math.ceil(
                (double) totalElements / size
        );

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("content", content);
        response.put("page", page);
        response.put("size", size);
        response.put("totalElements", totalElements);
        response.put("totalPages", totalPages);
        response.put("first", page == 0);
        response.put(
                "last",
                totalPages == 0 || page >= totalPages - 1
        );
        response.put("numberOfElements", content.size());
        response.put("empty", content.isEmpty());

        return response;
    }
}

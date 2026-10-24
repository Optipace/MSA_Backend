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

    public Map<String, Object> getAssessorReport(int page, int size) {

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page number cannot be negative");
        }

        if (size <= 0) {
            throw new IllegalArgumentException(
                    "Page size must be greater than zero");
        }

        int offset = Math.multiplyExact(page, size);

        String sql = """
            WITH report_data AS (
                SELECT
                    s.assessment_session_id AS "sessionId",
                    e.employee_code AS employeeId,

                    CONCAT_WS(
                        ' ',
                        e.first_name,
                        e.middle_name,
                        e.last_name
                    ) AS employee,

                    d.department_name AS department,
                    f.factory_name AS factory,

                    CONCAT(
                        COUNT(
                            CASE
                                WHEN ms.status_code = 'COMPLETED'
                                THEN 1
                            END
                        ),
                        '/',
                        COUNT(ami.assessment_module_instance_id)
                    ) AS assessment,

                    CONCAT(
                        ROUND(
                            COALESCE(
                                fs.percentage,
                                s.percentage,
                                0
                            )::numeric,
                            0
                        ),
                        '%'
                    ) AS "overallScore",

                    COALESCE(
                        fs.overall_result,
                        aa.result,
                        'N/A'
                    ) AS result,

                    s.assigned_on AS assignedOn

                FROM assessment.assessment_session s

                JOIN master.employee e
                    ON s.employee_id = e.employee_id

                LEFT JOIN master.department d
                    ON e.department_id = d.department_id

                LEFT JOIN master.factory f
                    ON e.factory_id = f.factory_id

                LEFT JOIN assessment.assessment_attempt aa
                    ON s.assessment_session_id =
                       aa.assessment_session_id

                LEFT JOIN assessment.assessment_module_instance ami
                    ON aa.assessment_attempt_id =
                       ami.assessment_attempt_id

                LEFT JOIN assessment.module_status ms
                    ON ami.module_status_id = ms.module_status_id

                LEFT JOIN assessment.final_score fs
                    ON s.assessment_session_id =
                       fs.assessment_session_id

                GROUP BY
                    s.assessment_session_id,
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
            ORDER BY assignedOn DESC NULLS LAST, "sessionId" DESC
            LIMIT ? OFFSET ?
            """;

        log.debug(
                "Executing paginated assessor report: page={}, size={}",
                page, size);

        List<Map<String, Object>> rows = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {

                    Map<String, Object> report =
                            new LinkedHashMap<>();

                    report.put("sessionId",
                            rs.getObject("sessionId"));
                    report.put("employeeId",
                            rs.getString("employeeId"));
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
                size,
                offset
        );

        /*
         * COUNT(*) OVER() returns the total number of report rows
         * before pagination, provided the requested page has rows.
         */
        long totalElements = rows.isEmpty()
                ? 0L
                : ((Number) rows.get(0).get("totalElements"))
                        .longValue();

        // Remove the internal count field from each report row.
        List<Map<String, Object>> content = new ArrayList<>();

        for (Map<String, Object> row : rows) {
            Map<String, Object> report =
                    new LinkedHashMap<>(row);

            report.remove("totalElements");
            content.add(report);
        }

        int totalPages = (int) Math.ceil(
                (double) totalElements / size);

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("content", content);
        response.put("page", page);
        response.put("size", size);
        response.put("totalElements", totalElements);
        response.put("totalPages", totalPages);
        response.put("first", page == 0);
        response.put("last",
                totalPages == 0 || page >= totalPages - 1);
        response.put("numberOfElements", content.size());
        response.put("empty", content.isEmpty());

        return response;
    }
}
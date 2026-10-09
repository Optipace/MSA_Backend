package org.optipace.adminService.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AssessorReportRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> getAssessorReport() {

        String sql = """
            SELECT
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
                            WHEN ms.status_code = 'COMPLETED' THEN 1
                        END
                    ),
                    '/',
                    COUNT(ami.assessment_module_instance_id)
                ) AS assessment,

                CONCAT(
                    ROUND(
                        COALESCE(fs.percentage, s.percentage, 0)::numeric,
                        0
                    ),
                    '%'
                ) AS overallScore,

                COALESCE(
                    fs.overall_result,
                    aa.result,
                    'N/A'
                ) AS result

            FROM assessment.assessment_session s

            JOIN master.employee e
                ON s.employee_id = e.employee_id

            LEFT JOIN master.department d
                ON e.department_id = d.department_id

            LEFT JOIN master.factory f
                ON e.factory_id = f.factory_id

            LEFT JOIN assessment.assessment_attempt aa
                ON s.assessment_session_id = aa.assessment_session_id

            LEFT JOIN assessment.assessment_module_instance ami
                ON aa.assessment_attempt_id = ami.assessment_attempt_id

            LEFT JOIN assessment.module_status ms
                ON ami.module_status_id = ms.module_status_id

            LEFT JOIN assessment.final_score fs
                ON s.assessment_session_id = fs.assessment_session_id

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

            ORDER BY s.assigned_on DESC
            """;

        log.debug("Executing assessor report SQL");

        return jdbcTemplate.query(sql, (rs, rowNum) -> {

            Map<String, Object> report = new LinkedHashMap<>();

            report.put("employeeId", rs.getString("employeeId"));
            report.put("employee", rs.getString("employee"));
            report.put("department", rs.getString("department"));
            report.put("factory", rs.getString("factory"));
            report.put("assessment", rs.getString("assessment"));
            report.put("overallScore", rs.getString("overallScore"));
            report.put("result", rs.getString("result"));

            return report;
        });
    }
}
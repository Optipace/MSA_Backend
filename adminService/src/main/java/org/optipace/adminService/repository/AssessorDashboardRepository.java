package org.optipace.adminService.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AssessorDashboardRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Returns dashboard summary and employee assessment details
     * in one response.
     */
    public Map<String, Object> getAssessorDashboard() {

        Map<String, Object> dashboard = new LinkedHashMap<>();

        String summarySql = """
            SELECT
                (SELECT COUNT(*)
                 FROM master.employee
                 WHERE record_status = 'A') AS total_employees,

                (SELECT COUNT(*)
                 FROM assessment.assessment_module_instance
                 WHERE module_status_id = 4) AS assessment_completed,

                (SELECT COUNT(*)
                 FROM assessment.assessment_module_instance
                 WHERE module_status_id = 2) AS assessment_pending,

                (SELECT COALESCE(AVG(percentage), 0)
                 FROM assessment.assessment_module_instance
                 WHERE module_status_id = 4) AS average_score
            """;

        log.debug("Executing assessor dashboard summary SQL");

        Map<String, Object> summary = jdbcTemplate.queryForObject(
                summarySql,
                (rs, rowNum) -> {
                    Map<String, Object> result = new LinkedHashMap<>();

                    result.put("totalEmployees",
                            rs.getLong("total_employees"));

                    result.put("assessmentCompleted",
                            rs.getLong("assessment_completed"));

                    result.put("assessmentPending",
                            rs.getLong("assessment_pending"));

                    result.put("averageScore",
                            rs.getBigDecimal("average_score"));

                    return result;
                }
        );

        dashboard.put("summary", summary);

        String employeeSql = """
            SELECT
                CONCAT_WS(' ', e.first_name, e.middle_name, e.last_name)
                    AS name,
                e.employee_code AS id,
                d.department_name AS department,
                f.factory_name AS factory,
                CAST(s.assigned_on AS date) AS assessment_date,
                st.status_name AS status
            FROM assessment.assessment_session s
            JOIN master.employee e
                ON s.employee_id = e.employee_id
            LEFT JOIN master.department d
                ON e.department_id = d.department_id
            LEFT JOIN master.factory f
                ON e.factory_id = f.factory_id
            LEFT JOIN assessment.assessment_status st
                ON s.assessment_status_id = st.assessment_status_id
            ORDER BY s.assigned_on DESC
            """;

        log.debug("Executing assessor dashboard employee SQL");

        List<Map<String, Object>> employees = jdbcTemplate.query(
                employeeSql,
                (rs, rowNum) -> {
                    Map<String, Object> employee = new LinkedHashMap<>();

                    employee.put("name", rs.getString("name"));
                    employee.put("id", rs.getString("id"));
                    employee.put("department", rs.getString("department"));
                    employee.put("factory", rs.getString("factory"));

                    Date assignedDate = rs.getDate("assessment_date");

                    employee.put(
                            "date",
                            assignedDate != null
                                    ? assignedDate.toLocalDate()
                                    : null
                    );

                    employee.put("status", rs.getString("status"));

                    return employee;
                }
        );

        dashboard.put("employees", employees);

        return dashboard;
    }
}
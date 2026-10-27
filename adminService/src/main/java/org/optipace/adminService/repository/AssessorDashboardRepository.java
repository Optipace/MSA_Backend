
package org.optipace.adminService.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.optipace.adminService.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
     * Returns dashboard summary and paginated employee assessment details
     * for the specified organization and optional factory.
     */
    public Map<String, Object> getAssessorDashboard(
            Long organizationId,
            Long factoryId,
            int page,
            int size) {

        if (organizationId == null) {
            throw new IllegalArgumentException(
                    "Organization ID is required"
            );
        }

        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException(
                    "Page must be >= 0 and size must be > 0"
            );
        }

        Map<String, Object> dashboard = new LinkedHashMap<>();

        // Dashboard summary filtered by organization and optional factory.
        String summarySql = """
            SELECT
                (
                    SELECT COUNT(*)
                    FROM master.employee e
                    JOIN master.factory f
                        ON e.factory_id = f.factory_id
                    WHERE e.record_status = 'A'
                      AND f.organization_id = ?
                      AND (? IS NULL OR e.factory_id = ?)
                ) AS total_employees,

                (
                    SELECT COUNT(*)
                    FROM assessment.assessment_module_instance ami
                    JOIN assessment.assessment_attempt aa
                        ON ami.assessment_attempt_id =
                           aa.assessment_attempt_id
                    JOIN assessment.assessment_session s
                        ON aa.assessment_session_id =
                           s.assessment_session_id
                    JOIN master.employee e
                        ON s.employee_id = e.employee_id
                    JOIN master.factory f
                        ON e.factory_id = f.factory_id
                    WHERE ami.module_status_id = 4
                      AND e.record_status = 'A'
                      AND f.organization_id = ?
                      AND (? IS NULL OR e.factory_id = ?)
                ) AS assessment_completed,

                (
                    SELECT COUNT(*)
                    FROM assessment.assessment_module_instance ami
                    JOIN assessment.assessment_attempt aa
                        ON ami.assessment_attempt_id =
                           aa.assessment_attempt_id
                    JOIN assessment.assessment_session s
                        ON aa.assessment_session_id =
                           s.assessment_session_id
                    JOIN master.employee e
                        ON s.employee_id = e.employee_id
                    JOIN master.factory f
                        ON e.factory_id = f.factory_id
                    WHERE ami.module_status_id = 2
                      AND e.record_status = 'A'
                      AND f.organization_id = ?
                      AND (? IS NULL OR e.factory_id = ?)
                ) AS assessment_pending,

                (
                    SELECT COALESCE(AVG(ami.percentage), 0)
                    FROM assessment.assessment_module_instance ami
                    JOIN assessment.assessment_attempt aa
                        ON ami.assessment_attempt_id =
                           aa.assessment_attempt_id
                    JOIN assessment.assessment_session s
                        ON aa.assessment_session_id =
                           s.assessment_session_id
                    JOIN master.employee e
                        ON s.employee_id = e.employee_id
                    JOIN master.factory f
                        ON e.factory_id = f.factory_id
                    WHERE ami.module_status_id = 4
                      AND e.record_status = 'A'
                      AND f.organization_id = ?
                      AND (? IS NULL OR e.factory_id = ?)
                ) AS average_score
            """;

        log.debug(
                "Executing assessor dashboard summary for organizationId={}, factoryId={}",
                organizationId,
                factoryId
        );

        Map<String, Object> summary = jdbcTemplate.queryForObject(
                summarySql,
                (rs, rowNum) -> {
                    Map<String, Object> result = new LinkedHashMap<>();

                    result.put(
                            "totalEmployees",
                            rs.getLong("total_employees")
                    );
                    result.put(
                            "assessmentCompleted",
                            rs.getLong("assessment_completed")
                    );
                    result.put(
                            "assessmentPending",
                            rs.getLong("assessment_pending")
                    );
                    result.put(
                            "averageScore",
                            rs.getBigDecimal("average_score")
                    );

                    return result;
                },
                organizationId, factoryId, factoryId,
                organizationId, factoryId, factoryId,
                organizationId, factoryId, factoryId,
                organizationId, factoryId, factoryId
        );

        dashboard.put("summary", summary);

        // Count active employees, including those without assessments.
        // This count is used for employee pagination.
        String countSql = """
            SELECT COUNT(*)
            FROM master.employee e
            JOIN master.factory f
                ON e.factory_id = f.factory_id
            WHERE e.record_status = 'A'
              AND f.organization_id = ?
              AND (? IS NULL OR e.factory_id = ?)
            """;

        Long totalElements = jdbcTemplate.queryForObject(
                countSql,
                Long.class,
                organizationId,
                factoryId,
                factoryId
        );

        // Select only the latest assessment session for each employee.
        // Employees without any assessment are retained by the LEFT JOIN.
        String employeeSql = """
            WITH latest_assessment AS (
                SELECT
                    s.*,
                    ROW_NUMBER() OVER (
                        PARTITION BY s.employee_id
                        ORDER BY
                            s.assigned_on DESC NULLS LAST,
                            s.assessment_session_id DESC
                    ) AS rn
                FROM assessment.assessment_session s
            )
            SELECT
                CONCAT_WS(
                    ' ',
                    e.first_name,
                    e.middle_name,
                    e.last_name
                ) AS name,
                e.employee_id AS empId,
                e.employee_code AS empCode,
                s.assessment_template_id AS assessmentTemplateId,
                d.department_name AS department,
                f.factory_name AS factory,
                CAST(s.assigned_on AS date) AS assessment_date,
                COALESCE(st.status_name, 'Not Assigned') AS status
            FROM master.employee e
            JOIN master.factory f
                ON e.factory_id = f.factory_id
            LEFT JOIN latest_assessment s
                ON s.employee_id = e.employee_id
               AND s.rn = 1
            LEFT JOIN master.department d
                ON e.department_id = d.department_id
            LEFT JOIN assessment.assessment_status st
                ON s.assessment_status_id =
                   st.assessment_status_id
            WHERE e.record_status = 'A'
              AND f.organization_id = ?
              AND (? IS NULL OR e.factory_id = ?)
            ORDER BY
                s.assigned_on DESC NULLS LAST,
                e.employee_id
            LIMIT ? OFFSET ?
            """;

        log.debug(
                "Executing paginated employee SQL: organizationId={}, factoryId={}, page={}, size={}",
                organizationId,
                factoryId,
                page,
                size
        );

        List<Map<String, Object>> employees = jdbcTemplate.query(
                employeeSql,
                (rs, rowNum) -> {
                    Map<String, Object> employee = new LinkedHashMap<>();

                    employee.put("name", rs.getString("name"));
                    employee.put("empId", rs.getObject("empId"));
                    employee.put("empCode", rs.getString("empCode"));
                    employee.put(
                            "assessmentTemplateId",
                            rs.getObject("assessmentTemplateId")
                    );
                    employee.put(
                            "department",
                            rs.getString("department")
                    );
                    employee.put(
                            "factory",
                            rs.getString("factory")
                    );

                    Date assignedDate =
                            rs.getDate("assessment_date");

                    employee.put(
                            "date",
                            assignedDate != null
                                    ? assignedDate.toLocalDate()
                                    : null
                    );

                    employee.put(
                            "status",
                            rs.getString("status")
                    );

                    return employee;
                },
                organizationId,
                factoryId,
                factoryId,
                size,
                (long) page * size
        );

        Page<Map<String, Object>> employeePage = new PageImpl<>(
                employees,
                PageRequest.of(page, size),
                totalElements != null ? totalElements : 0L
        );

        dashboard.put(
                "employees",
                PageResponse.of(employeePage)
        );

        return dashboard;
    }
}

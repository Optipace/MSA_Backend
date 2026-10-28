
package org.optipace.adminService.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class AssessorDashboardRepository {

    private final JdbcTemplate jdbcTemplate;

    public Map<String, Object> getAssessorDashboard(
            Long organisationId,
            Long factoryId,
            int page,
            int size) {

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page must be greater than or equal to 0");
        }

        if (size <= 0) {
            throw new IllegalArgumentException(
                    "Size must be greater than 0");
        }

        Map<String, Object> response = new LinkedHashMap<>();

        // Dashboard summary query
        String summarySql = """
            SELECT
                (
                    SELECT COUNT(*)
                    FROM master.employee e
                    WHERE e.record_status = 'A'
                ) AS "totalEmployees",

                (
                    SELECT COUNT(*)
                    FROM assessment.assessment_module_instance ami
                    JOIN assessment.assessment_attempt aa
                        ON aa.assessment_attempt_id =
                           ami.assessment_attempt_id
                    JOIN assessment.assessment_session s
                        ON s.assessment_session_id =
                           aa.assessment_session_id
                    JOIN master.employee e
                        ON e.employee_id = s.employee_id
                    WHERE ami.module_status_id = 4
                      AND e.record_status = 'A'
                ) AS "assessmentCompleted",

                (
                    SELECT COUNT(*)
                    FROM assessment.assessment_module_instance ami
                    JOIN assessment.assessment_attempt aa
                        ON aa.assessment_attempt_id =
                           ami.assessment_attempt_id
                    JOIN assessment.assessment_session s
                        ON s.assessment_session_id =
                           aa.assessment_session_id
                    JOIN master.employee e
                        ON e.employee_id = s.employee_id
                    WHERE ami.module_status_id = 2
                      AND e.record_status = 'A'
                ) AS "assessmentPending",

                (
                    SELECT COALESCE(AVG(ami.percentage), 0)
                    FROM assessment.assessment_module_instance ami
                    JOIN assessment.assessment_attempt aa
                        ON aa.assessment_attempt_id =
                           ami.assessment_attempt_id
                    JOIN assessment.assessment_session s
                        ON s.assessment_session_id =
                           aa.assessment_session_id
                    JOIN master.employee e
                        ON e.employee_id = s.employee_id
                    WHERE ami.module_status_id = 4
                      AND e.record_status = 'A'
                ) AS "averageScore"
            """;

        Map<String, Object> summary =
                jdbcTemplate.queryForMap(summarySql);

        response.put("summary", summary);

        // Employee query: include all active employees,
        // even when no assessment session exists.
        String employeeSql = """
            SELECT
                CONCAT_WS(
                    ' ',
                    e.first_name,
                    e.middle_name,
                    e.last_name
                ) AS "name",
                e.employee_id AS "empId",
                e.employee_code AS "empCode",
                s.assessment_template_id AS "assessmentTemplateId",
                d.department_name AS "department",
                f.factory_name AS "factory",
                CAST(s.assigned_on AS DATE) AS "date",
                COALESCE(ast.status_name, 'Not Assigned') AS "status"
            FROM master.employee e
            LEFT JOIN assessment.assessment_session s
                ON s.employee_id = e.employee_id
            LEFT JOIN master.department d
                ON d.department_id = e.department_id
            LEFT JOIN master.factory f
                ON f.factory_id = e.factory_id
            LEFT JOIN assessment.assessment_status ast
                ON ast.assessment_status_id =
                   s.assessment_status_id
            WHERE e.record_status = 'A'
            ORDER BY
                s.assigned_on DESC NULLS LAST,
                e.employee_id
            """;

        // Apply pagination
        String paginatedEmployeeSql = employeeSql + """
        	    LIMIT ? OFFSET ?
        	    """;

        	List<Map<String, Object>> employees =
        	        jdbcTemplate.queryForList(
        	                paginatedEmployeeSql,
        	                size,
        	                (long) page * size
        	        );

        	// Count all active employees for pagination metadata
        	String countSql = """
        	    SELECT COUNT(*)
        	    FROM master.employee e
        	    WHERE e.record_status = 'A'
        	    """;

        	Long totalElements = jdbcTemplate.queryForObject(
        	        countSql,
        	        Long.class
        	);

        	long total = totalElements != null ? totalElements : 0L;
        	int totalPages = (int) Math.ceil((double) total / size);

        	Map<String, Object> employeePage = new LinkedHashMap<>();
        	employeePage.put("content", employees);
        	employeePage.put("page", page);
        	employeePage.put("size", size);
        	employeePage.put("totalElements", total);
        	employeePage.put("totalPages", totalPages);
        	employeePage.put("first", page == 0);
        	employeePage.put("last", page >= totalPages - 1);
        	employeePage.put("numberOfElements", employees.size());
        	employeePage.put("empty", employees.isEmpty());

        	response.put("employees", employeePage);

        	return response;
    }
}

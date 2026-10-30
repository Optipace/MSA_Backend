
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

        /*
         * 1. Dashboard summary
         */
        String summarySql = """
            SELECT
                (
                    SELECT COUNT(*)
                    FROM master.employee e
                    WHERE e.record_status = 'A'
                      AND (? IS NULL OR e.organization_id = ?)
                      AND (? IS NULL OR e.factory_id = ?)
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
                      AND (? IS NULL OR e.organization_id = ?)
                      AND (? IS NULL OR e.factory_id = ?)
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
                      AND (? IS NULL OR e.organization_id = ?)
                      AND (? IS NULL OR e.factory_id = ?)
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
                      AND (? IS NULL OR e.organization_id = ?)
                      AND (? IS NULL OR e.factory_id = ?)
                ) AS "averageScore"
            """;

        Map<String, Object> summary = jdbcTemplate.queryForMap(
                summarySql,
                organisationId, organisationId,
                factoryId, factoryId,

                organisationId, organisationId,
                factoryId, factoryId,

                organisationId, organisationId,
                factoryId, factoryId,

                organisationId, organisationId,
                factoryId, factoryId
        );

        response.put("summary", summary);

        /*
         * 2. Employee list
         *
         * Existing session:
         *     Use its assessment template.
         *
         * No session:
         *     Match employee designation, department
         *     and experience against template eligibility.
         *
         * No eligibility match:
         *     Use active default template.
         */

        String employeeSql = """
            SELECT
                CONCAT_WS(
                    ' ',
                    NULLIF(TRIM(e.first_name), ''),
                    NULLIF(TRIM(e.middle_name), ''),
                    NULLIF(TRIM(e.last_name), '')
                ) AS "name",

                e.employee_id AS "empId",

                e.employee_code AS "empCode",

                COALESCE(
                    s.assessment_template_id,
                    eligible.assessment_template_id,
                    default_template.assessment_template_id
                ) AS "assessmentTemplateId",

                d.department_name AS "department",

                f.factory_name AS "factory",

                CAST(s.assigned_on AS DATE) AS "date",

                COALESCE(
                    ast.status_name,
                    'Not Assigned'
                ) AS "status"

            FROM master.employee e

            /*
             * Latest assessment session
             */
            LEFT JOIN LATERAL (
                SELECT
                    s1.assessment_template_id,
                    s1.assigned_on,
                    s1.assessment_status_id

                FROM assessment.assessment_session s1

                WHERE s1.employee_id = e.employee_id

                ORDER BY
                    s1.assigned_on DESC NULLS LAST,
                    s1.assessment_session_id DESC

                LIMIT 1
            ) s ON TRUE

            /*
             * Employee eligible assessment template
             */
            LEFT JOIN LATERAL (
                SELECT
                    ate.assessment_template_id

                FROM config.assessment_template_eligibility ate

                JOIN config.assessment_template at
                    ON at.assessment_template_id =
                       ate.assessment_template_id

                WHERE ate.designation_id = e.designation_id

                  AND ate.department_id = e.department_id

                  AND e.experience_years >= ate.minimum_experience

                  AND at.record_status = 'A'

                  AND CURRENT_DATE >= at.effective_from

                  AND (
                      at.effective_to IS NULL
                      OR CURRENT_DATE <= at.effective_to
                  )

                ORDER BY
                    ate.minimum_experience DESC,
                    ate.assessment_template_id

                LIMIT 1
            ) eligible ON TRUE

            /*
             * Default assessment template
             */
            LEFT JOIN LATERAL (
                SELECT
                    at.assessment_template_id

                FROM config.assessment_template at

                WHERE at.is_default = TRUE

                  AND at.record_status = 'A'

                  AND CURRENT_DATE >= at.effective_from

                  AND (
                      at.effective_to IS NULL
                      OR CURRENT_DATE <= at.effective_to
                  )

                ORDER BY at.assessment_template_id

                LIMIT 1
            ) default_template ON TRUE

            LEFT JOIN master.department d
                ON d.department_id = e.department_id

            LEFT JOIN master.factory f
                ON f.factory_id = e.factory_id

            LEFT JOIN assessment.assessment_status ast
                ON ast.assessment_status_id =
                   s.assessment_status_id

            WHERE e.record_status = 'A'

              AND (? IS NULL OR e.organization_id = ?)

              AND (? IS NULL OR e.factory_id = ?)

            ORDER BY
                s.assigned_on DESC NULLS LAST,
                e.employee_id
            """;

        /*
         * 3. Apply pagination
         */
        String paginatedEmployeeSql = employeeSql + """
            LIMIT ? OFFSET ?
            """;

        List<Map<String, Object>> employees =
                jdbcTemplate.queryForList(
                        paginatedEmployeeSql,
                        organisationId, organisationId,
                        factoryId, factoryId,
                        size,
                        (long) page * size
                );

        /*
         * 4. Count employees matching the same filters
         */
        String countSql = """
            SELECT COUNT(*)
            FROM master.employee e
            WHERE e.record_status = 'A'
              AND (? IS NULL OR e.organization_id = ?)
              AND (? IS NULL OR e.factory_id = ?)
            """;

        Long totalElements = jdbcTemplate.queryForObject(
                countSql,
                Long.class,
                organisationId, organisationId,
                factoryId, factoryId
        );

        long total = totalElements != null
                ? totalElements
                : 0L;

        int totalPages = (int) Math.ceil(
                (double) total / size
        );

        /*
         * 5. Build pagination response
         */
        Map<String, Object> employeePage = new LinkedHashMap<>();

        employeePage.put("content", employees);
        employeePage.put("page", page);
        employeePage.put("size", size);
        employeePage.put("totalElements", total);
        employeePage.put("totalPages", totalPages);
        employeePage.put("first", page == 0);
        employeePage.put(
                "last",
                totalPages == 0 || page >= totalPages - 1
        );
        employeePage.put("numberOfElements", employees.size());
        employeePage.put("empty", employees.isEmpty());

        response.put("employees", employeePage);

        return response;
    }
}

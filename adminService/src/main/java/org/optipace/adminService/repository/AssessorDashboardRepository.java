package org.optipace.adminService.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AssessorDashboardRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Returns:
     * 1. Total employees
     * 2. Assessment completed
     * 3. Assessment pending
     */
    public Map<String, Object> getDashboardSummary() {

        String sql = """
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

        log.debug("Executing assessor dashboard count SQL: {}", sql);

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {

            Map<String, Object> result = new LinkedHashMap<>();

            result.put("totalEmployees", rs.getLong("total_employees"));
            result.put("assessmentCompleted",
                    rs.getLong("assessment_completed"));
            result.put("assessmentPending",
                    rs.getLong("assessment_pending"));
            result.put("averageScore", rs.getBigDecimal("average_score"));

            return result;
        });
    }
}
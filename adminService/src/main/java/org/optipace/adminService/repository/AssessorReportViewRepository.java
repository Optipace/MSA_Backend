package org.optipace.adminService.repository;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class AssessorReportViewRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Query 1: Fetch summary metadata for Header and Summary Cards
     */
    public Map<String, Object> getAssessmentSummaryBySessionId(UUID sessionId) {
        String sql = """
            SELECT 
                CONCAT_WS(' ', e.first_name, e.middle_name, e.last_name) AS employeeName,
                e.employee_code AS employeeCode,
                d.department_name AS departmentName,
                f.factory_name AS factoryName,
                ROUND(COALESCE(fs.percentage, s.percentage, 0)::numeric, 0) AS overallScore,
                COUNT(ami.assessment_module_instance_id) AS totalModules,
                COUNT(CASE WHEN ms.status_code = 'COMPLETED' THEN 1 END) AS completedModules,
                COUNT(CASE WHEN ms.status_code = 'COMPLETED' AND (ami.percentage >= t.pass_percentage) THEN 1 END) AS passedCount,
                COUNT(CASE WHEN ms.status_code = 'COMPLETED' AND (ami.percentage < t.pass_percentage) THEN 1 END) AS failedCount
            FROM assessment.assessment_session s
            JOIN master.employee e 
                ON s.employee_id = e.employee_id
            LEFT JOIN master.department d 
                ON e.department_id = d.department_id
            LEFT JOIN master.factory f 
                ON e.factory_id = f.factory_id
            LEFT JOIN config.assessment_template t 
                ON s.assessment_template_id = t.assessment_template_id
            LEFT JOIN assessment.assessment_attempt aa 
                ON s.assessment_session_id = aa.assessment_session_id
            LEFT JOIN assessment.assessment_module_instance ami 
                ON aa.assessment_attempt_id = ami.assessment_attempt_id
            LEFT JOIN assessment.module_status ms 
                ON ami.module_status_id = ms.module_status_id
            LEFT JOIN assessment.final_score fs 
                ON s.assessment_session_id = fs.assessment_session_id
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
     * Query 2: Fetch test breakdown for Bar Chart & Table Rows
     */
    public List<Map<String, Object>> getTestBreakdownBySessionId(UUID sessionId) {
        String sql = """
            SELECT 
                am.assessment_module_id AS moduleId,
                am.module_name AS testName,
                CAST(COALESCE(ami.completed_on, ami.started_on, s.assigned_on) AS date) AS testDate,
                ROUND(COALESCE(ami.percentage, 0)::numeric, 0) AS percentage,
                CASE 
                    WHEN ami.percentage >= t.pass_percentage THEN 'Passed'
                    ELSE 'Failed'
                END AS result
            FROM assessment.assessment_session s
            JOIN config.assessment_template t 
                ON s.assessment_template_id = t.assessment_template_id
            JOIN assessment.assessment_attempt aa 
                ON s.assessment_session_id = aa.assessment_session_id
            JOIN assessment.assessment_module_instance ami 
                ON aa.assessment_attempt_id = ami.assessment_attempt_id
            JOIN config.assessment_module am 
                ON ami.assessment_module_id = am.assessment_module_id
            WHERE s.assessment_session_id = ?
            ORDER BY am.display_order
        """;

        // Fixed: queryForList instead of queryList
        return jdbcTemplate.queryForList(sql, sessionId);
    }
}
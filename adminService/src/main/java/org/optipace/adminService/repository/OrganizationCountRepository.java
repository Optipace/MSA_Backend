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
public class OrganizationCountRepository {
    
    private final JdbcTemplate jdbcTemplate;
    
    /**
     * Returns all counts in one query:
     *   total, active, inactive, deleted
     */
    public Map<String, Object> getOrganizationCountSummary() {
        
        String sql = """
            SELECT 
                COUNT(*) AS total,
                COALESCE(SUM(CASE WHEN record_status = 'A' THEN 1 ELSE 0 END), 0) AS active,
                COALESCE(SUM(CASE WHEN record_status = 'I' THEN 1 ELSE 0 END), 0) AS inactive,
                COALESCE(SUM(CASE WHEN record_status = 'D' THEN 1 ELSE 0 END), 0) AS deleted
            FROM master.organization
        """;
        
        log.debug("Executing count SQL: {}", sql);
        
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("total",    rs.getLong("total"));
            result.put("active",   rs.getLong("active"));
            result.put("inactive", rs.getLong("inactive"));
            result.put("deleted",  rs.getLong("deleted"));
            return result;
        });
    }
    
    /**
     * Count only — no breakdown.
     */
    public long countAll() {
        String sql = "SELECT COUNT(*) FROM master.organization";
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count != null ? count : 0L;
    }
    
    /**
     * Count by record_status.
     */
    public long countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM master.organization WHERE record_status = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, status);
        return count != null ? count : 0L;
    }
    
    /**
     * Count created by a specific admin.
     */
    public long countByCreatedBy(Long adminId) {
        String sql = "SELECT COUNT(*) FROM master.organization WHERE created_by = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, adminId);
        return count != null ? count : 0L;
    }
}
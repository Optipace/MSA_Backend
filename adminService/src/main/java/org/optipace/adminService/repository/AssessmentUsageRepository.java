package org.optipace.adminService.repository;

import java.util.List;

import org.optipace.adminService.dto.request.SuperAdminAssessmentReportDto;
import org.optipace.adminService.entity.AssessmentUsage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AssessmentUsageRepository extends JpaRepository<AssessmentUsage, Long> {
	
	 // Find assessments for an organization
    List<AssessmentUsage> findByOrganizationIdOrderByAssignedAtDesc(Long organizationId);
    
    // Find assessments assigned by a specific assessor
    List<AssessmentUsage> findByAssessorId(Long assessorId);
	
    // ⭐ Super Admin Report Query with Pagination
    @Query(value = """
        SELECT 
            e.employee_code AS employeeId, 
            CONCAT(e.first_name, ' ', COALESCE(e.last_name, '')) AS employeeName,
            COALESCE(d.department_name, 'N/A') AS department,
            a.score AS score,
            a.status AS status,
            CAST(a.completed_at AS DATE) AS date
        FROM assessment.assessment_usage a
        JOIN master.employee e ON a.assessed_employee_id = e.employee_id
        LEFT JOIN master.department d ON e.department_id = d.department_id
        WHERE (:organizationId IS NULL OR a.organization_id = :organizationId)
        ORDER BY a.completed_at DESC
    """, 
    countQuery = """
        SELECT COUNT(a.usage_id) 
        FROM assessment.assessment_usage a
        WHERE (:organizationId IS NULL OR a.organization_id = :organizationId)
    """,
    nativeQuery = true)
    Page<SuperAdminAssessmentReportDto> getSuperAdminAssessmentReport(
            @Param("organizationId") Long organizationId, 
            Pageable pageable
    );
}
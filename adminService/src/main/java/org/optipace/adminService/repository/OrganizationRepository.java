package org.optipace.adminService.repository;

import java.util.List;

import org.optipace.adminService.dto.response.OrganizationUsageResponse;
import org.optipace.adminService.dto.response.OrgAdminCountResponse;
import org.optipace.adminService.dto.response.OrganizationOverviewReportResponse;
import org.optipace.adminService.dto.response.OrganizationOverviewResponse;
import org.optipace.adminService.entity.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
	
	 // ⭐ Native Query to count Admins per Organization
    @Query(value = """
        SELECT 
            o.organization_id AS organizationId,
            o.organization_name AS organizationName,
            COUNT(CASE WHEN r.role_code IN ('ADMIN', 'FACTORY_ADMIN', 'SUPER_ADMIN') THEN 1 END) AS adminCount
        FROM master.organization o
        LEFT JOIN master.employee e ON o.organization_id = e.organization_id
        LEFT JOIN security.employee_role er ON e.employee_id = er.employee_id
        LEFT JOIN security.role r ON er.role_id = r.role_id
        GROUP BY o.organization_id, o.organization_name
        ORDER BY adminCount DESC, o.organization_name ASC
    """, nativeQuery = true)
    List<OrgAdminCountResponse> getOrganizationAdminCounts();
    
    
    @Query(value = """
            SELECT 
                o.organization_id AS organizationId,
                o.organization_name AS organizationName,
                COALESCE(
                    (SELECT CONCAT(e.first_name, ' ', COALESCE(e.last_name, ''))
                     FROM master.employee e
                     JOIN security.employee_role er ON e.employee_id = er.employee_id
                     JOIN security.role r ON er.role_id = r.role_id
                     WHERE e.organization_id = o.organization_id 
                       AND r.role_code IN ('FACTORY_ADMIN', 'ADMIN', 'SUPER_ADMIN')
                     ORDER BY er.is_primary_role DESC, e.employee_id ASC
                     LIMIT 1),
                'No Admin Assigned') AS adminName,
                COALESCE(
                    (SELECT p.package_name 
                     FROM packages.organization_package op
                     JOIN packages.package p ON op.package_id = p.package_id
                     WHERE op.organization_id = o.organization_id 
                       AND op.subscription_status = 'ACTIVE'
                       AND (op.end_date >= CURRENT_DATE OR op.end_date IS NULL)
                     ORDER BY op.end_date DESC
                     LIMIT 1), 
                'No Active Plan') AS planName,
                (SELECT COUNT(*) FROM master.employee e WHERE e.organization_id = o.organization_id) AS userCount,
                CASE 
                    WHEN o.record_status = 'A' THEN 'Active'
                    WHEN o.record_status = 'I' THEN 'Inactive'
                    ELSE 'Deleted'
                END AS status
            FROM master.organization o
            WHERE o.created_by = :superAdminId
            ORDER BY o.created_on DESC
        """, nativeQuery = true)
        // ⭐ Return type MUST be the inner class
        List<OrganizationOverviewReportResponse.OrganizationRow> getOrganizationOverviewByAdmin(@Param("superAdminId") Long superAdminId);
 
    @Query(
            value = """
                SELECT 
                    o.organization_name AS "organizationName",
                    p.package_name AS "planName",
                    op.total_points AS "totalUsage",
                    op.used_points AS "used",
                    op.available_points AS "remaining",
                    op.expiry_date AS "validUntil"
                FROM packages.organization_points op
                JOIN master.organization o ON op.organization_id = o.organization_id
                JOIN packages.organization_package org_pkg ON op.organization_package_id = org_pkg.organization_package_id
                JOIN packages.package p ON org_pkg.package_id = p.package_id
                WHERE (:search IS NULL OR LOWER(o.organization_name) LIKE LOWER(CONCAT('%', :search, '%')))
                ORDER BY op.created_on DESC
                """,
            countQuery = """
                SELECT COUNT(*)
                FROM packages.organization_points op
                JOIN master.organization o ON op.organization_id = o.organization_id
                JOIN packages.organization_package org_pkg ON op.organization_package_id = org_pkg.organization_package_id
                JOIN packages.package p ON org_pkg.package_id = p.package_id
                WHERE (:search IS NULL OR LOWER(o.organization_name) LIKE LOWER(CONCAT('%', :search, '%')))
                """,
            nativeQuery = true
        )
        Page<OrganizationUsageResponse> findAllOrganizationUsage(
                @Param("search") String search,
                Pageable pageable
        );
    
    // 👇 Add this native query to get the org ID from the factory admin ID
    @Query(value = "SELECT organization_id FROM master.employee WHERE employee_id = :factoryAdminId", nativeQuery = true)
    Long findOrganizationIdByEmployeeId(@Param("factoryAdminId") Long factoryAdminId);
}
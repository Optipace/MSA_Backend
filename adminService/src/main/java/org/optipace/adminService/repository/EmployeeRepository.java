package org.optipace.adminService.repository;

import java.util.List;

import org.optipace.adminService.dto.response.AdminListResponse;
import org.optipace.adminService.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // ⭐ Query returns a single Long value
    @Query(value = """
        SELECT COUNT(DISTINCT e.employee_id)
        FROM master.employee e
        JOIN security.employee_role er ON e.employee_id = er.employee_id
        JOIN security.role r ON er.role_id = r.role_id
        WHERE r.role_code IN ('FACTORY_ADMIN')
       -- WHERE r.role_code IN ('SUPER_ADMIN', 'FACTORY_ADMIN', 'ADMIN')
          AND (:userAdminId IS NULL OR e.created_by = :userAdminId)
    """, nativeQuery = true)
    Long countTotalAdmins(@Param("userAdminId") Long userAdminId);
    
	/*
	 * @Query(value = """ SELECT e.employee_id AS employeeId, e.employee_code AS
	 * adminCode, CONCAT(e.first_name, ' ', COALESCE(e.last_name, '')) AS adminName,
	 * e.email AS email, e.mobile_number AS phoneNumber,
	 * COALESCE(o.organization_name, 'N/A') AS organizationName, r.role_code AS
	 * roleName, CASE WHEN e.record_status = 'A' THEN 'Active' ELSE 'Inactive' END
	 * AS status FROM master.employee e LEFT JOIN master.organization o ON
	 * e.organization_id = o.organization_id JOIN security.employee_role er ON
	 * e.employee_id = er.employee_id JOIN security.role r ON er.role_id = r.role_id
	 * WHERE e.created_by = :superAdminId AND r.role_code IN ('FACTORY_ADMIN') ORDER
	 * BY e.created_on DESC """, nativeQuery = true) List<AdminListResponse>
	 * findAllAdminsBySuperAdmin(@Param("superAdminId") Long superAdminId);
	 */
    
    @Query(
    	    value = """
    	        SELECT 
    	            e.employee_id AS "employeeId",
    	            e.employee_code AS "adminCode",
    	            CONCAT(e.first_name, ' ', COALESCE(e.last_name, '')) AS "adminName",
    	            e.email AS "email",
    	            e.mobile_number AS "phoneNumber",
    	            COALESCE(o.organization_name, 'N/A') AS "organizationName",
    	            COALESCE(c.city_name, 'N/A') AS "location",
    	            r.role_code AS "roleName",
    	            CASE WHEN e.record_status = 'A' THEN 'Active' ELSE 'Inactive' END AS "status"
    	        FROM master.employee e
    	        LEFT JOIN master.organization o ON e.organization_id = o.organization_id
    	        LEFT JOIN master.city c ON o.city_id = c.city_id
    	        JOIN security.employee_role er ON e.employee_id = er.employee_id
    	        JOIN security.role r ON er.role_id = r.role_id
    	        WHERE e.created_by = :superAdminId
    	          AND r.role_code IN ('FACTORY_ADMIN')
    	        ORDER BY e.created_on DESC
    	        """,
    	    countQuery = """
    	        SELECT COUNT(*)
    	        FROM master.employee e
    	        JOIN security.employee_role er ON e.employee_id = er.employee_id
    	        JOIN security.role r ON er.role_id = r.role_id
    	        WHERE e.created_by = :superAdminId
    	          AND r.role_code IN ('FACTORY_ADMIN')
    	        """,
    	    nativeQuery = true
    	)
    	Page<AdminListResponse> findAllAdminsBySuperAdmin(
    	        @Param("superAdminId") Long superAdminId,
    	        Pageable pageable);
    @Modifying
    @Transactional
    @Query(value = "UPDATE security.employee_login SET account_locked = :locked WHERE employee_id = :employeeId", nativeQuery = true)
    void updateLoginLockStatus(@Param("employeeId") Long employeeId, @Param("locked") boolean locked);
}
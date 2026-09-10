package org.optipace.authService.repository;

import org.optipace.authService.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
    @Query("SELECT rp FROM RolePermission rp JOIN FETCH rp.permission WHERE rp.role.roleId = :roleId")
    List<RolePermission> findByRole_RoleIdWithPermissions(@Param("roleId") Long roleId);

    boolean existsByRole_RoleIdAndPermission_PermissionId(Long roleId, Long permissionId);
}

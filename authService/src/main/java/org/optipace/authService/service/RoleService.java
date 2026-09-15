package org.optipace.authService.service;

import org.optipace.authService.dto.requestDto.AssignRoleToEmployeeRequest;
import org.optipace.authService.dto.responseDto.PermissionResponse;
import org.optipace.authService.dto.responseDto.RolesResponse;
import org.optipace.authService.dto.responseDto.SingleResponse;

import java.util.List;

public interface RoleService {
    SingleResponse<List<RolesResponse>> getAllRoles();

    SingleResponse<?> assignRoleToEmployee(AssignRoleToEmployeeRequest request, String adminId);

    SingleResponse<List<PermissionResponse>> getPermissionsForRole(Long roleId);

    SingleResponse<?> assignPermissionsToRole(Long roleId, Long permissionId, String adminId);
}

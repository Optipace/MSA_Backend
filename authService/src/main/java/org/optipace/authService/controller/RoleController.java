package org.optipace.authService.controller;

import lombok.RequiredArgsConstructor;
import org.optipace.authService.dto.requestDto.AssignRoleToEmployeeRequest;
import org.optipace.authService.dto.responseDto.PermissionResponse;
import org.optipace.authService.dto.responseDto.RolesResponse;
import org.optipace.authService.dto.responseDto.SingleResponse;
import org.optipace.authService.service.RoleService;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/role")
@RestController
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/v1/all")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<List<RolesResponse>>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @PostMapping("/v1/assignRoleToEmployee")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<?>> assignRoleToEmployee(@RequestBody AssignRoleToEmployeeRequest request, @RequestHeader("X-User-Id") String adminId){
        return ResponseEntity.ok(roleService.assignRoleToEmployee(request, adminId));
    }

    @GetMapping("/v1/{roleId}/permissions")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<List<PermissionResponse>>> getRolePermissions(@PathVariable Long roleId) {
        return ResponseEntity.ok(roleService.getPermissionsForRole(roleId));
    }

    @PostMapping("/v1/assignPermissionsToRole")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<?>> assignPermissionsToRole(@RequestParam("roleId") Long roleId,
                                                                     @RequestParam("permissionId") Long permissionId,
                                                                     @RequestHeader("X-User-Id") String adminId){
        return ResponseEntity.ok(roleService.assignPermissionsToRole(roleId,permissionId, adminId));
    }
}

package org.optipace.authService.service.serviceImpl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.optipace.authService.dto.requestDto.AssignRoleToEmployeeRequest;
import org.optipace.authService.dto.responseDto.PermissionResponse;
import org.optipace.authService.dto.responseDto.Response;
import org.optipace.authService.dto.responseDto.RolesResponse;
import org.optipace.authService.dto.responseDto.SingleResponse;
import org.optipace.authService.entity.*;
import org.optipace.authService.exception.BadRequestException;
import org.optipace.authService.exception.NotFoundException;
import org.optipace.authService.repository.*;
import org.optipace.authService.service.RoleService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final EmployeeLoginRepository employeeLoginRepository;
    private final EmployeeRoleRepository employeeRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public SingleResponse<List<RolesResponse>> getAllRoles() {
        List<RolesResponse> roles = roleRepository.findByRecordStatusAndIsSystemRoleFalse('A')
                .stream()
                .map(r -> new RolesResponse(r.getRoleId(), r.getRoleCode(), r.getRoleName()))
                .toList();
        return new SingleResponse<>(
                roles,
                new Response(200, "SUCCESS")
        );
    }

    @Override
    @Transactional
    public SingleResponse<?> assignRoleToEmployee(AssignRoleToEmployeeRequest request, String adminId) {

        EmployeeLogin employeeLogin = employeeLoginRepository.findByEmployeeId(request.getEmployeeId())
                .orElseThrow(() -> new NotFoundException("Employee not found"));

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new NotFoundException("Role not found"));

        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        employeeRoleRepository.findByEmployeeIdAndIsPrimaryRoleTrue(employeeLogin.getEmployeeId())
                .ifPresent(oldPrimaryRole -> {
                    oldPrimaryRole.setIsPrimaryRole(false);
                    oldPrimaryRole.setEffectiveTo(today);
                    employeeRoleRepository.save(oldPrimaryRole);
                });

        EmployeeRole employeeRole = new EmployeeRole();
        employeeRole.setEmployeeId(employeeLogin.getEmployeeId());
        employeeRole.setRole(role);
        employeeRole.setCreatedBy(Long.parseLong(adminId));

        employeeRole.setIsPrimaryRole(true);
        employeeRole.setEffectiveFrom(tomorrow);

        employeeRoleRepository.save(employeeRole);

        return new SingleResponse<>(
                null,
                new Response(200, "Success")
        );
    }

    @Override
    public SingleResponse<List<PermissionResponse>> getPermissionsForRole(Long roleId) {

        if (!roleRepository.existsById(roleId)) {
            throw new NotFoundException("Role not found with ID: " + roleId);
        }

        List<PermissionResponse> permissions = rolePermissionRepository.findByRole_RoleIdWithPermissions(roleId)
                .stream()
                .map(rp -> {
                    var permission = rp.getPermission();
                    return new PermissionResponse(
                            permission.getPermissionId(),
                            permission.getPermissionCode(),
                            permission.getPermissionName(),
                            permission.getModuleName(),
                            permission.getDescription()
                    );
                })
                .toList();

        return new SingleResponse<>(
                permissions,
                new Response(200, "SUCCESS")
        );
    }

    @Override
    @Transactional
    public SingleResponse<?> assignPermissionsToRole(Long roleId, Long permissionId, String adminId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new NotFoundException("Role not found"));

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new NotFoundException("Permission not found"));

        if (rolePermissionRepository.existsByRole_RoleIdAndPermission_PermissionId(roleId, permissionId)) {
            throw new BadRequestException("The role '" + role.getRoleName() + "' already has this permission.");
        }

        RolePermission rolePermission = new RolePermission();
        rolePermission.setPermission(permission);
        rolePermission.setRole(role);
        rolePermission.setCreatedBy(Long.parseLong(adminId));

        rolePermissionRepository.save(rolePermission);

        return new SingleResponse<>(
                null,
                new Response(
                        200,
                        "Success"
                )
        );
    }
}

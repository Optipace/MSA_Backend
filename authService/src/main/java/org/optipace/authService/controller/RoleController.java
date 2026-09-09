package org.optipace.authService.controller;

import lombok.RequiredArgsConstructor;
import org.optipace.authService.dto.responseDto.RolesResponse;
import org.optipace.authService.dto.responseDto.SingleResponse;
import org.optipace.authService.service.RoleService;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

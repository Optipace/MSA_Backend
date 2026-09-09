package org.optipace.authService.service.serviceImpl;

import lombok.AllArgsConstructor;
import org.optipace.authService.dto.responseDto.Response;
import org.optipace.authService.dto.responseDto.RolesResponse;
import org.optipace.authService.dto.responseDto.SingleResponse;
import org.optipace.authService.repository.RoleRepository;
import org.optipace.authService.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

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
}

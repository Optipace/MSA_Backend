package org.optipace.authService.service;

import org.optipace.authService.dto.responseDto.RolesResponse;
import org.optipace.authService.dto.responseDto.SingleResponse;

import java.util.List;

public interface RoleService {
    SingleResponse<List<RolesResponse>> getAllRoles();
}

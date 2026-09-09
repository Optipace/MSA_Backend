package org.optipace.authService.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RolesResponse {
    private Long roleId;
    private String roleCode;
    private String roleName;
}

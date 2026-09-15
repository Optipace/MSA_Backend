package org.optipace.authService.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PermissionResponse {
    private Long permissionId;
    private String permissionCode;
    private String permissionName;
    private String moduleName;
    private String description;
}

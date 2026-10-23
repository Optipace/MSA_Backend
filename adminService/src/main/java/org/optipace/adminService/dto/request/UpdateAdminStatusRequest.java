package org.optipace.adminService.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAdminStatusRequest {

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "^[AID]$", message = "Status must be 'A' (Active), 'I' (Inactive), or 'D' (Deleted)")
    private String status; // 'A', 'I', or 'D'
}
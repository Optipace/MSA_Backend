package org.optipace.adminService.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeductPointsRequest {
    
    @NotNull(message = "Organization ID is required")
    private Long organizationId;
    
    @NotNull(message = "Factory ID is required")
    private Long factoryId;
    
    @NotNull(message = "Assessed Employee ID is required")
    private Long assessedEmployeeId;
    
    @NotNull(message = "Assessor ID is required")
    private Long assessorId;
    
    @NotNull(message = "Points to deduct is required")
    @Min(1)
    private Long points;
    
    @NotNull(message = "Reference ID (Session ID) is required")
    private Long referenceId;
    
    @NotNull(message = "Module Code is required")
    private String moduleCode;      // e.g. "IQ_TEST"
}
package org.optipace.masterService.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateStateRequest {

    private Long countryId;

    @Size(max = 10, message = "State code cannot exceed 10 characters")
    private String stateCode;

    @Size(max = 100, message = "State name cannot exceed 100 characters")
    private String stateName;

    @Size(max = 5, message = "GST state code cannot exceed 5 characters")
    private String gstStateCode;

    private Integer displayOrder;

    private String remarks;
}

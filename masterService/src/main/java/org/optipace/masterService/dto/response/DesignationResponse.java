package org.optipace.masterService.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DesignationResponse {
    private Long designationId;
    private String designationCode;
    private String designationName;
    private int hierarchyLevel;
    private Boolean isManager;
    private String remarks;
}

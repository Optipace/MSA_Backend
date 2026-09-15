package org.optipace.masterService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MasterOrganizationResponse {
    private Long organizationId;
    private String organizationCode;
    private String organizationName;
}

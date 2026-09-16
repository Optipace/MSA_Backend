package org.optipace.masterService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ListOfStateResponse {

    private Long stateId;

    private Long countryId;

    private String stateCode;

    private String stateName;

    private String gstStateCode;

    private Integer displayOrder;
}

package org.optipace.masterService.dto.request;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class UpdateCityRequest {

    private Long stateId;

    private String cityCode;

    private String cityName;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private String remarks;
}



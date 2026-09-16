package org.optipace.masterService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CityResponse {

    private Long cityId;

    private Long stateId;

    private String cityCode;

    private String cityName;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private String remarks;
}



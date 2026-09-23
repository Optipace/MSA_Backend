package org.optipace.garmentService.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListOfGarmentAreaResponse {

    private Long garmentAreaId;

    private Long garmentTypeId;

    private String areaCode;

    private String areaName;

    private Integer displayOrder;

    private BigDecimal xCoordinate;

    private BigDecimal yCoordinate;

    private BigDecimal width;

    private BigDecimal height;
}
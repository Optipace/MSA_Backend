package org.optipace.garmentService.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class GarmentLotUpdateRequest {

    private String lotNumber;

    private Long garmentTypeId;

    private Long factoryId;

    private LocalDate productionDate;

    private Integer quantity;

    private String buyerName;

    private String purchaseOrderNo;

    private String color;

    private String size;

    private String remarks;
}
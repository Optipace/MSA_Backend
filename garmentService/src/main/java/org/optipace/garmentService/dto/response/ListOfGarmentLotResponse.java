package org.optipace.garmentService.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class ListOfGarmentLotResponse {

    private UUID garmentLotId;

    private String lotNumber;

    private Long garmentTypeId;

    private Long factoryId;

    private LocalDate productionDate;

    private Integer quantity;

    private String buyerName;

    private String purchaseOrderNo;

    private String color;

    private String size;

    private String recordStatus;
}
package org.optipace.garmentService.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class GarmentLotResponse {

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

    private String remarks;

    private Long createdBy;

    private OffsetDateTime createdOn;

    private Long updatedBy;

    private OffsetDateTime updatedOn;

    private Integer versionNo;

    private String recordStatus;
}
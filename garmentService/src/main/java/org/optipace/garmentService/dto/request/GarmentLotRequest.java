package org.optipace.garmentService.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class GarmentLotRequest {

    @NotBlank
    private String lotNumber;

    @NotNull
    private Long garmentTypeId;

    @NotNull
    private Long factoryId;

    private LocalDate productionDate;

    @NotNull
    private Integer quantity;

    private String buyerName;

    private String purchaseOrderNo;

    private String color;

    private String size;

    private String remarks;
}
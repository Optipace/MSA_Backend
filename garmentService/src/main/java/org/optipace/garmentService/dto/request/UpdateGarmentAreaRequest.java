package org.optipace.garmentService.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateGarmentAreaRequest {

    @NotNull(message = "Garment type ID is required")
    private Long garmentTypeId;

    @NotBlank(message = "Area code is required")
    @Size(max = 30, message = "Area code cannot exceed 30 characters")
    private String areaCode;

    @NotBlank(message = "Area name is required")
    @Size(max = 100, message = "Area name cannot exceed 100 characters")
    private String areaName;

    private Integer displayOrder;

    private BigDecimal xCoordinate;

    private BigDecimal yCoordinate;

    private BigDecimal width;

    private BigDecimal height;
}
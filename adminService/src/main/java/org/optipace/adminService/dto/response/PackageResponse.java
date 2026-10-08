package org.optipace.adminService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PackageResponse {
    
    private Long packageId;
    private String packageName;
    private String usageDescription;
    private BigDecimal price;
    private String currency;
    private Integer numberOfDays;
    private Long usage;
    private String recordStatus;
    private Long createdBy;
    private OffsetDateTime createdOn;
    private Long updatedBy;
    private OffsetDateTime updatedOn;
}
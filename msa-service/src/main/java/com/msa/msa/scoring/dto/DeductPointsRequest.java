package com.msa.msa.scoring.dto;

import lombok.Data;

//Create this in msa-service
@Data
public class DeductPointsRequest {
 private Long organizationId;
 private Long factoryId;
 private Long assessedEmployeeId;
 private Long assessorId;
 private Long points;
 private Long referenceId;
 private String moduleCode;
}
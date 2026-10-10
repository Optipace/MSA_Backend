package org.optipace.adminService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopMissedAreaItemResponse {

    private Integer rank;
    private String areaName;
    private Double percentage;
    private Long employeeCount;
}
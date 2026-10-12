package org.optipace.adminService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IqTestDistributionItemResponse {

    private Long categoryId;
    private String categoryName;
    private Long employeeCount;
    private Double percentage;
}
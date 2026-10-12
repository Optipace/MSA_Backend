package org.optipace.adminService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IqTestDistributionResponse {

    private Long totalEmployeesAssessed;
    private List<IqTestDistributionItemResponse> data;
}
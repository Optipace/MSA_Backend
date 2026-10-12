package org.optipace.adminService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColourTestDistributionResponse {

    private Long totalEmployees;

    private List<ColourTestDistributionItemResponse> distribution;
}
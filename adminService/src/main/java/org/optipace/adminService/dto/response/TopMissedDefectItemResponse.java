package org.optipace.adminService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopMissedDefectItemResponse {

    private int rank;
    private String defectName;
    private double percentage;
    private Long employeeCount;
}
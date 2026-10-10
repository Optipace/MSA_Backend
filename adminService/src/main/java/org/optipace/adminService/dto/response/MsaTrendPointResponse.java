package org.optipace.adminService.dto.response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MsaTrendPointResponse {
    private Integer year;
    private Integer month;
    private String monthLabel;
    private BigDecimal averageScore;
}
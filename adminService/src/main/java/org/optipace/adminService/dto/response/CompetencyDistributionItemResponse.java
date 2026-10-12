package org.optipace.adminService.dto.response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompetencyDistributionItemResponse {

    private Long competencyRatingId;
    private String ratingCode;
    private String ratingName;
    private String colorCode;
    private Long employeeCount;
    private Double percentage;
}
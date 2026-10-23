package org.optipace.adminService.dto.response;
import lombok.Data;

@Data
public class AssessmentReportResponse {
    private String assessmentName;
    private Integer assigned;
    private Integer completed;
    private Integer passed;
    private Integer failed;
    private String avgScore;
    private String status;
}

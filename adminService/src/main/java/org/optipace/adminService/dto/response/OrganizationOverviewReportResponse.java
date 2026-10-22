package org.optipace.adminService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationOverviewReportResponse {
    private Long totalOrganizations;
    private List<OrganizationRow> organizations;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrganizationRow {
        private Long organizationId;
        private String organizationName;
        private String adminName;
        private String planName;
        private Long userCount;
        private String status;
    }
}
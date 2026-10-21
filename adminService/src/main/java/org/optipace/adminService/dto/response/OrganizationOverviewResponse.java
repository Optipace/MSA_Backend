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
public class OrganizationOverviewResponse {
    
    private Long totalOrganizations;
    
    // ⭐ This is the list of rows (Notice it uses the nested class below)
    private List<OrganizationRow> organizations;
    
    // ⭐ Inner Static Class for a single row
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
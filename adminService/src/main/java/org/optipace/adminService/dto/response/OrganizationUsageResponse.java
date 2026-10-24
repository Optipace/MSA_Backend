package org.optipace.adminService.dto.response;
import java.time.LocalDate;

public interface OrganizationUsageResponse {
    String getOrganizationName();
    String getPlanName();
    Long getTotalUsage();
    Long getUsed();
    Long getRemaining();
    LocalDate getValidUntil();
}
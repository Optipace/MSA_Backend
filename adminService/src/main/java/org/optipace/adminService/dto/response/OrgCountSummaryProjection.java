package org.optipace.adminService.dto.response;

public interface OrgCountSummaryProjection {
    Long getTotal();
    Long getActive();
    Long getInactive();
    Long getDeleted();
}
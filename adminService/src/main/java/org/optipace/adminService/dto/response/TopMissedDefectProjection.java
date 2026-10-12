package org.optipace.adminService.dto.response;

public interface TopMissedDefectProjection {

    String getDefectName();

    Long getEmployeeCount();

    Long getMissedCount();

    Long getTotalCount();
}
package org.optipace.adminService.dto.response;

public interface TopMissedAreaProjection {

    String getAreaName();

    Long getMissedCount();

    Long getTotalCount();

    Long getEmployeeCount();
}
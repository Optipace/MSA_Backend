package org.optipace.adminService.dto.response;
public interface AdminCountProjection {
    Long getAdminId();
    Long getTotal();
    Long getActive();
    Long getInactive();
    Long getDeleted();
}
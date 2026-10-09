
package org.optipace.adminService.repository;

import org.optipace.adminService.dto.response.DashboardSummaryProjection;

public interface DashboardRepository {

    DashboardSummaryProjection getDashboardSummary();
}
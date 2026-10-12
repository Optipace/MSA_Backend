package org.optipace.adminService.dto.response;

import java.math.BigDecimal;

public interface DashboardSummaryProjection {

    Long getTotalEmployeesAssessed();
    Long getTestsTaken();
    Long getTestsPending();
    Long getTestsFailed();
    Long getGoodPerformance();
}

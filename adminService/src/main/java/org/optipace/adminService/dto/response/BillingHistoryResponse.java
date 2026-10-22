package org.optipace.adminService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingHistoryResponse {
    private LocalDateTime date;
    private String transaction;    // Initial Purchase, Plan Upgrade, Add Usage
    private String packageName;    // Starter, Gold, etc. (or "-")
    private Long usage;            // Points bought/added
    private String billingCycle;   // Monthly, Yearly
    private BigDecimal amount;
    private String status;         // Paid, Pending, Failed
    private String invoice;        // Invoice URL or ID
}
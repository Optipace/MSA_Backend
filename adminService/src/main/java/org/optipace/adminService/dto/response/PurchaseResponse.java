package org.optipace.adminService.dto.response;

import lombok.Data;
import org.optipace.adminService.enums.Currency;
import org.optipace.adminService.enums.PaymentMethod;
import org.optipace.adminService.enums.PaymentStatus;
import org.optipace.adminService.enums.SubscriptionStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PurchaseResponse {
    
    // Payment
    private Long paymentId;
    private String transactionId;
    private BigDecimal amount;
    private Currency currency;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private String gatewayOrderId;
    
    // OrganizationPackage
    private Long organizationPackageId;
    private Long packageId;
    private String packageName;
    private LocalDate startDate;
    private LocalDate endDate;
    private SubscriptionStatus subscriptionStatus;
    
    private LocalDateTime createdOn;
}
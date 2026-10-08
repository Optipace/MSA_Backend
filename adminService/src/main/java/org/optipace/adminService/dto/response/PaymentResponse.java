package org.optipace.adminService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.optipace.adminService.enums.Currency;
import org.optipace.adminService.enums.PaymentMethod;
import org.optipace.adminService.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    
    private Long paymentId;
    private Long organizationId;
    private Long organizationPackageId;
    private String packageName;
    private Long paidBy;
    private BigDecimal amount;
    private Currency currency;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private String transactionId;
    private String gatewayOrderId;
    private String gatewayPaymentId;
    private LocalDateTime paymentDate;
    private String failureReason;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
}
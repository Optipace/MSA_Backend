package org.optipace.adminService.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCallbackRequestDto {
    
    private String transactionId;          // internal reference
    private String gatewayOrderId;
    private String gatewayPaymentId;
    private String gatewaySignature;
    private Boolean success;
    private String failureReason;
}
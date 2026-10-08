package org.optipace.adminService.service;

import org.optipace.adminService.dto.request.PaymentCallbackRequestDto;
import org.optipace.adminService.dto.request.PurchaseRequestDto;
import org.optipace.adminService.dto.response.PaymentResponse;
import org.optipace.adminService.dto.response.PurchaseResponse;
import org.optipace.adminService.dto.response.SingleResponse;
import org.springframework.data.domain.Pageable;

public interface PurchaseService {
    
    SingleResponse<PaymentResponse> initiatePurchase(PurchaseRequestDto request, String userId);
    
   // SingleResponse<PaymentResponse> handleCallback(PaymentCallbackRequestDto callback);
    
    SingleResponse<PaymentResponse> getPaymentById(Long paymentId);
    
    SingleResponse<?> getAllPayments(Pageable pageable);
    
    SingleResponse<?> getPaymentsByOrganization(Long organizationId, Pageable pageable);
    /** FACTORY_ADMIN initiates a purchase */
    SingleResponse<PurchaseResponse> buyPackage(PurchaseRequestDto request, String userId);
    
    /** Payment gateway callback — success or failure */
    SingleResponse<PurchaseResponse> handleCallback(PaymentCallbackRequestDto callback);
}
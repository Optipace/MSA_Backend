package org.optipace.adminService.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.optipace.adminService.dto.request.PaymentCallbackRequestDto;
import org.optipace.adminService.dto.request.PurchaseRequestDto;
import org.optipace.adminService.dto.response.PaymentResponse;
import org.optipace.adminService.dto.response.PurchaseResponse;
import org.optipace.adminService.dto.response.SingleResponse;
import org.optipace.adminService.entity.OrganizationPackage;
import org.optipace.adminService.entity.Payment;
import org.optipace.adminService.entity.SubscriptionPackage;
import org.optipace.adminService.enums.*;
import org.optipace.adminService.exception.BadRequestException;
import org.optipace.adminService.exception.ResourceNotFoundException;
import org.optipace.adminService.repository.*;
import org.optipace.adminService.service.PurchaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {
    
    private final PaymentRepository paymentRepository;
   // private final SubscriptionPackageRepository subscriptionPackageRepository;
    private final OrganizationPackageRepository organizationPackageRepository;
    private final SubscriptionPackageRepository subscriptionPackageRepository;

    // =====================================================
    // STEP 1 — FACTORY_ADMIN BUYS PACKAGE
    // =====================================================
    @Override
    @Transactional
    public SingleResponse<PurchaseResponse> buyPackage(PurchaseRequestDto request, String userId) {
        
        log.info("FACTORY_ADMIN {} buying package {} for organization {}",
                userId, request.getPackageId(), request.getOrganizationId());
        
        Long userIdLong = parseUserId(userId);
        
        // 1. Load master SubscriptionPackage
        SubscriptionPackage pkg = subscriptionPackageRepository
            .findById(request.getPackageId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "SubscriptionPackage", "packageId", request.getPackageId()));
        
        // 2. Validate package is active
        if (pkg.getRecordStatus() == null || pkg.getRecordStatus() != 'A') {
            throw new BadRequestException("Package is not available for purchase");
        }
        
        // 3. Create OrganizationPackage (PENDING)
        LocalDate today = LocalDate.now();
        OrganizationPackage orgPkg = OrganizationPackage.builder()
            .packageEntity(pkg)
            .organizationId(request.getOrganizationId())
           // .factoryId(request.getFactoryId())
            .purchasedBy(userIdLong)
            .startDate(today)
            .endDate(today.plusDays(pkg.getNumberOfDays()))
            .packagePrice(pkg.getPrice())
            .currency(Currency.valueOf(pkg.getCurrency()))
            .paymentStatus(PaymentStatus.PENDING)
            .subscriptionStatus(SubscriptionStatus.PENDING)
          //  .remarks(request.getRemarks())
            .createdBy(userIdLong)
            .build();
        
        OrganizationPackage savedOrgPkg = organizationPackageRepository.saveAndFlush(orgPkg);
        
        // 4. Create Payment (PENDING)
        String txnId = generateTransactionId();
        String gatewayOrderId = "ORDER-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        Payment payment = Payment.builder()
            .organizationPackage(savedOrgPkg)
            .organizationId(request.getOrganizationId())
            .paidBy(userIdLong)
            .amount(pkg.getPrice())
            .currency(Currency.valueOf(pkg.getCurrency()))
            .paymentMethod(request.getPaymentMethod())
            .paymentStatus(PaymentStatus.PENDING)
            .transactionId(txnId)
            .gatewayOrderId(gatewayOrderId)
            .build();
        
        Payment savedPayment = paymentRepository.saveAndFlush(payment);
        
        log.info("Purchase created. TxnId: {}, GatewayOrderId: {}", txnId, gatewayOrderId);
        
        // 5. Build response
        return new SingleResponse<PurchaseResponse>(
            buildResponse(savedPayment, savedOrgPkg, pkg),
            CustomStatus.SUCCESS
        );
    }
    
    // =====================================================
    // STEP 2 — PAYMENT CALLBACK (SUCCESS / FAILED)
    // =====================================================
    @Override
    @Transactional
    public SingleResponse<PurchaseResponse> handleCallback(PaymentCallbackRequestDto callback) {
        
        log.info("Payment callback: txn={}, success={}",
                callback.getTransactionId(), callback.getSuccess());
        
        // 1. Find payment
        Payment payment = paymentRepository
            .findByTransactionId(callback.getTransactionId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Payment", "transactionId", callback.getTransactionId()));
        
        // 2. Idempotency
        if (payment.getPaymentStatus() != PaymentStatus.PENDING) {
            log.warn("Payment already processed: {}", payment.getPaymentStatus());
            return new SingleResponse<PurchaseResponse>(
                buildResponse(payment, payment.getOrganizationPackage(), 
                              payment.getOrganizationPackage().getPackageEntity()),
                CustomStatus.SUCCESS);
        }
        
        // 3. Update gateway info
        if (callback.getGatewayOrderId() != null) {
            payment.setGatewayOrderId(callback.getGatewayOrderId());
        }
        if (callback.getGatewayPaymentId() != null) {
            payment.setGatewayPaymentId(callback.getGatewayPaymentId());
        }
        
        // 4. Set status
        if (Boolean.TRUE.equals(callback.getSuccess())) {
            payment.setPaymentStatus(PaymentStatus.SUCCESS);
            payment.setPaymentDate(LocalDateTime.now());
            payment.setUpdatedOn(LocalDateTime.now());
            
            // Update OrganizationPackage → ACTIVE
            OrganizationPackage orgPkg = payment.getOrganizationPackage();
            orgPkg.setPaymentStatus(PaymentStatus.SUCCESS);
            orgPkg.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
            orgPkg.setTransactionId(payment.getTransactionId());
            orgPkg.setUpdatedOn(LocalDateTime.now());
            organizationPackageRepository.saveAndFlush(orgPkg);
            
            // TODO: Credit points to factory here
            
        } else {
            payment.setPaymentStatus(PaymentStatus.FAILED);
            payment.setFailureReason(callback.getFailureReason() != null
                    ? callback.getFailureReason() : "Payment failed at gateway");
            payment.setUpdatedOn(LocalDateTime.now());
            
            // OrganizationPackage stays PENDING
            OrganizationPackage orgPkg = payment.getOrganizationPackage();
            orgPkg.setPaymentStatus(PaymentStatus.FAILED);
            orgPkg.setUpdatedOn(LocalDateTime.now());
            organizationPackageRepository.saveAndFlush(orgPkg);
        }
        
        Payment updated = paymentRepository.saveAndFlush(payment);
        
        return new SingleResponse<PurchaseResponse>(
            buildResponse(updated, updated.getOrganizationPackage(),
                          updated.getOrganizationPackage().getPackageEntity()),
            CustomStatus.SUCCESS
        );
    }
    
    // =====================================================
    // HELPERS
    // =====================================================
    private Long parseUserId(String userId) {
        try {
            return Long.parseLong(userId);
        } catch (NumberFormatException ex) {
            throw new BadRequestException("Invalid X-User-Id: " + userId);
        }
    }
    
    private String generateTransactionId() {
        return "TXN-" + System.currentTimeMillis() + "-"
                + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
    
    private PurchaseResponse buildResponse(Payment p, OrganizationPackage op, SubscriptionPackage pkg) {
        PurchaseResponse r = new PurchaseResponse();
        r.setPaymentId(p.getPaymentId());
        r.setTransactionId(p.getTransactionId());
        r.setAmount(p.getAmount());
        r.setCurrency(p.getCurrency());
        r.setPaymentMethod(p.getPaymentMethod());
        r.setPaymentStatus(p.getPaymentStatus());
        r.setGatewayOrderId(p.getGatewayOrderId());
        
        if (op != null) {
            r.setOrganizationPackageId(op.getOrganizationPackageId());
            r.setStartDate(op.getStartDate());
            r.setEndDate(op.getEndDate());
            r.setSubscriptionStatus(op.getSubscriptionStatus());
        }
        
        if (pkg != null) {
            r.setPackageId(pkg.getPackageId());
            r.setPackageName(pkg.getPackageName());
        }
        
        r.setCreatedOn(p.getCreatedOn());
        return r;
    }
    
 // =====================================================
 // GET PAYMENTS BY ORGANIZATION (paginated)
 // =====================================================
 @Override
 @Transactional(readOnly = true)
 public SingleResponse<?> getPaymentsByOrganization(Long organizationId, Pageable pageable) {
     log.info("Fetching payments for organization: {} — page: {}, size: {}",
              organizationId, pageable.getPageNumber(), pageable.getPageSize());
     
     Page<Payment> page = paymentRepository
         .findByOrganizationId(organizationId, pageable);
     
     // Build paginated response
     Map<String, Object> data = new LinkedHashMap<>();
     data.put("content", page.getContent().stream()
         .map(this::toResponse)
         .collect(Collectors.toList()));
     data.put("page", page.getNumber());
     data.put("size", page.getSize());
     data.put("totalElements", page.getTotalElements());
     data.put("totalPages", page.getTotalPages());
     data.put("last", page.isLast());
     data.put("first", page.isFirst());
     
     return new SingleResponse<Map<String, Object>>(data, CustomStatus.SUCCESS);
 }

 @Override
 public SingleResponse<PaymentResponse> initiatePurchase(PurchaseRequestDto request, String userId) {
	// TODO Auto-generated method stub
	return null;
 }

 @Override
 public SingleResponse<PaymentResponse> getPaymentById(Long paymentId) {
	// TODO Auto-generated method stub
	return null;
 }

 @Override
 public SingleResponse<?> getAllPayments(Pageable pageable) {
	// TODO Auto-generated method stub
	return null;
 }
 
 private PaymentResponse toResponse(Payment p) {
	    PaymentResponse r = new PaymentResponse();
	    r.setPaymentId(p.getPaymentId());
	    r.setOrganizationId(p.getOrganizationId());
	    r.setPaidBy(p.getPaidBy());
	    r.setAmount(p.getAmount());
	    r.setCurrency(p.getCurrency());
	    r.setPaymentMethod(p.getPaymentMethod());
	    r.setPaymentStatus(p.getPaymentStatus());
	    r.setTransactionId(p.getTransactionId());
	    r.setGatewayOrderId(p.getGatewayOrderId());
	    r.setGatewayPaymentId(p.getGatewayPaymentId());
	    r.setPaymentDate(p.getPaymentDate());
	    r.setFailureReason(p.getFailureReason());
	    r.setCreatedOn(p.getCreatedOn());
	    r.setUpdatedOn(p.getUpdatedOn());
	    return r;
	}
    
}
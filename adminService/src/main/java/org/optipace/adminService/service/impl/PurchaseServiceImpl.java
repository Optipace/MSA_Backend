package org.optipace.adminService.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.optipace.adminService.dto.request.CreateOrderRequest;
import org.optipace.adminService.dto.request.PaymentCallbackRequestDto;
import org.optipace.adminService.dto.request.PurchaseRequestDto;
import org.optipace.adminService.dto.response.CreateOrderResponse;
import org.optipace.adminService.dto.response.PaymentResponse;
import org.optipace.adminService.dto.response.PurchaseResponse;
import org.optipace.adminService.dto.response.SingleResponse;
import org.optipace.adminService.entity.OrganizationPackage;
import org.optipace.adminService.entity.OrganizationPoints;
import org.optipace.adminService.entity.Payment;
import org.optipace.adminService.entity.PointTransaction;
import org.optipace.adminService.entity.SubscriptionPackage;
import org.optipace.adminService.enums.*;
import org.optipace.adminService.exception.BadRequestException;
import org.optipace.adminService.exception.ResourceNotFoundException;
import org.optipace.adminService.repository.*;
import org.optipace.adminService.service.PurchaseService;
import org.optipace.adminService.service.RazorpayOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.optipace.adminService.dto.response.BillingHistoryResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {
    
    private final PaymentRepository paymentRepository;
    private final OrganizationPackageRepository organizationPackageRepository;
    private final SubscriptionPackageRepository subscriptionPackageRepository;
    private final PointTransactionRepository pointTransactionRepository;
    private final OrganizationPointsRepository organizationPointsRepository;
    
    private final RazorpayOrderService razorpayOrderService;

    // =====================================================
    // STEP 1 — FACTORY_ADMIN BUYS PACKAGE
    // =====================================================
    @Override
    @Transactional
    public SingleResponse<PurchaseResponse> buyPackage(PurchaseRequestDto request, String userId) {
        
        log.info("FACTORY_ADMIN {} buying package {} for organization {}, factory {}",
                userId, request.getPackageId(), request.getOrganizationId(), request.getFactoryId());
        
        Long userIdLong = parseUserId(userId);
        
        // ⭐ Idempotency — check for recent pending payment (last 2 minutes)
        LocalDateTime twoMinutesAgo = LocalDateTime.now().minusMinutes(2);
        
        Optional<Payment> recentPending = paymentRepository
            .findFirstByOrganizationIdAndPaidByAndPaymentStatusOrderByCreatedOnDesc(
                request.getOrganizationId(), userIdLong, PaymentStatus.PENDING);
        
        if (recentPending.isPresent()
            && recentPending.get().getCreatedOn().isAfter(twoMinutesAgo)) {
            
            log.warn("Duplicate request detected — returning existing pending payment: {}",
                     recentPending.get().getTransactionId());
            
            Payment existing = recentPending.get();
            OrganizationPackage existingOrgPkg = existing.getOrganizationPackage();
            
            return new SingleResponse<PurchaseResponse>(
                buildResponse(existing, existingOrgPkg, existingOrgPkg.getPackageEntity()),
                CustomStatus.SUCCESS
            );
        }
        
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
            .factoryId(request.getFactoryId())
            .purchasedBy(userIdLong)
            .startDate(today)
            .endDate(today.plusDays(pkg.getNumberOfDays()))
            .packagePrice(pkg.getPrice())
            .currency(Currency.valueOf(pkg.getCurrency()))
            .paymentStatus(PaymentStatus.PENDING)
            .subscriptionStatus(SubscriptionStatus.PENDING)
            //.pointsAllocated(0)
            //.pointsUsed(0)
            //.pointsBalance(0)
            .createdBy(userIdLong)
            .build();
        
        OrganizationPackage savedOrgPkg = organizationPackageRepository.saveAndFlush(orgPkg);
        
       // new code updated
        
        String txnId = generateTransactionId();

     // Convert package price from rupees to paise
     long amountPaise = pkg.getPrice()
             .movePointRight(2)
             .longValueExact();

     String currency = pkg.getCurrency();

  // Create Razorpay order request
     String receipt = "OPT-" + java.util.UUID.randomUUID()
     .toString().substring(0, 18);

     
     CreateOrderRequest rpay = new CreateOrderRequest();
     rpay.setAmount(amountPaise);
     rpay.setCurrency(currency);
     rpay.setReceipt(receipt);

     CreateOrderResponse response =
             razorpayOrderService.createOrder(rpay);
     
     String gatewayOrderId = response.getOrderId();
    // String gatewayOrderId = razorpayOrder.orderId();

     // Create payment record with PENDING status
     Payment payment = Payment.builder()
             .organizationPackage(savedOrgPkg)
             .organizationId(request.getOrganizationId())
             .paidBy(userIdLong)
             .amount(pkg.getPrice())
             .currency(Currency.valueOf(currency))
             .paymentMethod(request.getPaymentMethod())
             .paymentStatus(PaymentStatus.PENDING)
             .transactionId(txnId)
             .gatewayOrderId(gatewayOrderId)
             .build();

     Payment savedPayment = paymentRepository.saveAndFlush(payment);

     log.info(
             "Purchase created. TxnId: {}, Razorpay OrderId: {}",
             txnId,
             gatewayOrderId
     );
        
        // 4. Create Payment (PENDING)
		/*
		 * String txnId = generateTransactionId(); String gatewayOrderId = "ORDER-" +
		 * UUID.randomUUID().toString().substring(0, 8).toUpperCase();
		 * 
		 * Payment payment = Payment.builder() .organizationPackage(savedOrgPkg)
		 * .organizationId(request.getOrganizationId()) .paidBy(userIdLong)
		 * .amount(pkg.getPrice()) .currency(Currency.valueOf(pkg.getCurrency()))
		 * .paymentMethod(request.getPaymentMethod())
		 * .paymentStatus(PaymentStatus.PENDING) .transactionId(txnId)
		 * .gatewayOrderId(gatewayOrderId) .build();
		 * 
		 * Payment savedPayment = paymentRepository.saveAndFlush(payment);
		 * 
		 * log.info("Purchase created. TxnId: {}, GatewayOrderId: {}", txnId,
		 * gatewayOrderId);
		 */        
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
        
        // 4. Handle status
        if (Boolean.TRUE.equals(callback.getSuccess())) {
            
        	// Verify the Razorpay order against the server-side payment record
        	String storedOrderId = payment.getGatewayOrderId();
            String callbackOrderId = callback.getGatewayOrderId();
            String gatewayPaymentId = callback.getGatewayPaymentId();
            String signature = callback.getGatewaySignature();

            if (storedOrderId == null
                    || callbackOrderId == null
                    || gatewayPaymentId == null
                    || signature == null) {
                throw new IllegalArgumentException(
                        "Missing Razorpay payment verification details");
            }

            if (!storedOrderId.equals(callbackOrderId)) {
                throw new IllegalArgumentException(
                        "Razorpay order ID mismatch");
            }

            boolean validSignature = razorpayOrderService.verifySignature(
                    storedOrderId,
                    gatewayPaymentId,
                    signature
            );

            if (!validSignature) {
                throw new IllegalArgumentException(
                        "Invalid Razorpay payment signature");
            }
            
            // =====================================================
            // ✅ SUCCESS
            // =====================================================
            payment.setPaymentStatus(PaymentStatus.SUCCESS);
            payment.setPaymentDate(LocalDateTime.now());
            payment.setUpdatedOn(LocalDateTime.now());
            
            OrganizationPackage orgPkg = payment.getOrganizationPackage();
            orgPkg.setPaymentStatus(PaymentStatus.SUCCESS);
            orgPkg.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
            orgPkg.setTransactionId(payment.getTransactionId());
            orgPkg.setUpdatedOn(LocalDateTime.now());
            organizationPackageRepository.saveAndFlush(orgPkg);
            
            // ⭐ Calculate points
            SubscriptionPackage pkg = orgPkg.getPackageEntity();
            long pointsToCredit = calculatePoints(pkg);
            
            // =====================================================
            // ⭐ STEP A: INSERT INTO point_transaction (CREDIT)
            // =====================================================
			/*
			 * PointTransaction creditTx = PointTransaction.builder()
			 * .organizationId(orgPkg.getOrganizationId())
			 * .organizationPackageId(orgPkg.getOrganizationPackageId()) // ⭐ Long field
			 * .transactionType(PointTransactionType.CREDIT) .points(pointsToCredit)
			 * .referenceType("PURCHASE") .referenceId(payment.getPaymentId())
			 * .description("Points credited for package purchase. Txn: " +
			 * payment.getTransactionId()) .createdBy(payment.getPaidBy()) .build();
			 */
            
            PointTransaction creditTx = PointTransaction.builder()
            .organizationPackage(orgPkg)                                 // ⭐ pass the entity
            .organizationId(orgPkg.getOrganizationId())
            .transactionType(PointTransactionType.CREDIT)
            .points(pointsToCredit)
            .referenceType("PURCHASE")
            .referenceId(payment.getPaymentId())
            .description("Points credited for purchase")
            .createdBy(payment.getPaidBy())
            .build();
            
            pointTransactionRepository.saveAndFlush(creditTx);
            log.info("✅ point_transaction INSERTED — ID: {}, points: {}", 
                     creditTx.getPointTransactionId(), pointsToCredit);
            
            // =====================================================
            // ⭐ STEP B: INSERT/UPDATE organization_points
            // =====================================================
            OrganizationPoints orgPoints = organizationPointsRepository
                .findByOrganizationPackageId(orgPkg.getOrganizationPackageId())
                .orElse(null);
            
            if (orgPoints == null) {
                // INSERT — first time
                orgPoints = OrganizationPoints.builder()
                    .organizationId(orgPkg.getOrganizationId())
                    .organizationPackageId(orgPkg.getOrganizationPackageId())
                    .totalPoints(pointsToCredit)
                    .usedPoints(0L)
                    .availablePoints(pointsToCredit)
                    .expiryDate(orgPkg.getEndDate())              // ⭐ expiry from package end date
                    .isExpired(false)
                    .build();
                
                log.info("➕ organization_points INSERT — pkgId: {}, points: {}",
                         orgPkg.getOrganizationPackageId(), pointsToCredit);
            } else {
                // UPDATE — renewal
                orgPoints.setTotalPoints(orgPoints.getTotalPoints() + pointsToCredit);
                orgPoints.setAvailablePoints(orgPoints.getAvailablePoints() + pointsToCredit);
                orgPoints.setExpiryDate(orgPkg.getEndDate());
                orgPoints.setIsExpired(false);
                
                log.info("📈 organization_points UPDATE — pkgId: {}, newTotal: {}",
                         orgPkg.getOrganizationPackageId(), orgPoints.getTotalPoints());
            }
            
            OrganizationPoints savedPoints = organizationPointsRepository.saveAndFlush(orgPoints);
            log.info("✅ organization_points SAVED — ID: {}", savedPoints.getPointsId());
            
            log.info("✅ Points CREDITED — {} points to org {} (payment: {})",
                     pointsToCredit, orgPkg.getOrganizationId(), payment.getPaymentId());
            
        } else {
            
            // =====================================================
            // ❌ FAILED — no points credited
            // =====================================================
            payment.setPaymentStatus(PaymentStatus.FAILED);
            payment.setFailureReason(callback.getFailureReason() != null
                    ? callback.getFailureReason() : "Payment failed at gateway");
            payment.setUpdatedOn(LocalDateTime.now());
            
            OrganizationPackage orgPkg = payment.getOrganizationPackage();
            orgPkg.setPaymentStatus(PaymentStatus.FAILED);
            orgPkg.setUpdatedOn(LocalDateTime.now());
            organizationPackageRepository.saveAndFlush(orgPkg);
            
            log.warn("❌ Payment FAILED — no points credited");
        }
        
        Payment updated = paymentRepository.saveAndFlush(payment);
        
        return new SingleResponse<PurchaseResponse>(
            buildResponse(updated, updated.getOrganizationPackage(),
                          updated.getOrganizationPackage().getPackageEntity()),
            CustomStatus.SUCCESS
        );
    }
    
    // =====================================================
    // GET PAYMENT BY ID
    // =====================================================
    @Override
    @Transactional(readOnly = true)
    public SingleResponse<PaymentResponse> getPaymentById(Long paymentId) {
        log.info("Fetching payment: {}", paymentId);
        
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new ResourceNotFoundException("Payment", "paymentId", paymentId));
        
        return new SingleResponse<PaymentResponse>(toResponse(payment), CustomStatus.SUCCESS);
    }
    
    // =====================================================
    // GET ALL PAYMENTS (paginated)
    // =====================================================
    @Override
    @Transactional(readOnly = true)
    public SingleResponse<?> getAllPayments(Pageable pageable) {
        log.info("Fetching all payments — page: {}, size: {}",
                 pageable.getPageNumber(), pageable.getPageSize());
        
        Page<Payment> page = paymentRepository.findAll(pageable);
        
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
    
    // =====================================================
    // DEPRECATED / STUB — kept for backward compat
    // =====================================================
    @Override
    @Transactional
    public SingleResponse<PaymentResponse> initiatePurchase(PurchaseRequestDto request, String userId) {
        // Delegates to buyPackage — kept for backward compatibility
        log.info("initiatePurchase called — delegating to buyPackage");
        
        SingleResponse<PurchaseResponse> result = buyPackage(request, userId);
        
        // Build a PaymentResponse from the PurchaseResponse (simplified)
        PaymentResponse response = new PaymentResponse();
        if (result.getData() != null) {
            response.setPaymentId(result.getData().getPaymentId());
            response.setTransactionId(result.getData().getTransactionId());
            response.setAmount(result.getData().getAmount());
            response.setCurrency(result.getData().getCurrency());
            response.setPaymentMethod(result.getData().getPaymentMethod());
            response.setPaymentStatus(result.getData().getPaymentStatus());
            response.setGatewayOrderId(result.getData().getGatewayOrderId());
            response.setCreatedOn(result.getData().getCreatedOn());
        }
        return new SingleResponse<PaymentResponse>(response, CustomStatus.SUCCESS);
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
    
    /**
     * Calculate points for a package.
     * Uses the `usage` field as the points value.
     * Falls back to 1 point per ₹100 if usage is null.
     */
    private long calculatePoints(SubscriptionPackage pkg) {
        if (pkg.getUsage() != null && pkg.getUsage() > 0) {
            return pkg.getUsage();
        }
        // Fallback: 1 point per ₹100
        return pkg.getPrice() != null ? pkg.getPrice().longValue() / 100 : 0L;
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
    
 // =====================================================
 // ⭐ BILLING HISTORY REPORT
 // =====================================================
 @Override
 @Transactional(readOnly = true)
 public SingleResponse<List<BillingHistoryResponse>> getBillingHistory(Long organizationId) {
     
     log.info("Fetching billing history for organization: {}", organizationId);
     
     // 1. Fetch all payments for the org, newest first
     List<Payment> payments = paymentRepository.findByOrganizationIdOrderByPaymentDateDesc(organizationId);
     
     List<BillingHistoryResponse> history = new java.util.ArrayList<>();
     
     boolean isFirstPurchase = true;
     
     // 2. Iterate through payments (oldest to newest for correct logic, so reverse first if needed)
     // Actually, let's process oldest to newest to determine "Initial Purchase"
     List<Payment> chronologicalPayments = new java.util.ArrayList<>(payments);
     java.util.Collections.reverse(chronologicalPayments); 
     
     for (Payment payment : chronologicalPayments) {
         
         OrganizationPackage orgPkg = payment.getOrganizationPackage();
         if (orgPkg == null) continue;
         
         SubscriptionPackage pkg = orgPkg.getPackageEntity();
         
         // 3. Determine Transaction Type & Package Name
         String transactionType;
         String packageName;
         
         if (isFirstPurchase) {
             transactionType = "Initial Purchase";
             packageName = pkg != null ? pkg.getPackageName() : "Unknown";
             isFirstPurchase = false;
         } else {
             transactionType = "Add Usage"; // Or "Plan Upgrade" if you compare pkg IDs
             packageName = "-";
         }
         
         // 4. Determine Usage (Points)
         // We can read this from the PointTransaction linked to this payment
         Long usage = pointTransactionRepository
                 .findByReferenceTypeAndReferenceId("PURCHASE", payment.getPaymentId())
                 .stream().findFirst()
                 .map(PointTransaction::getPoints)
                 .orElse(0L);
                 
         // 5. Billing Cycle (Based on package number of days)
         String billingCycle = "One-Time";
         if (pkg != null && pkg.getNumberOfDays() != null) {
             if (pkg.getNumberOfDays() >= 28 && pkg.getNumberOfDays() <= 31) billingCycle = "Monthly";
             else if (pkg.getNumberOfDays() >= 360) billingCycle = "Yearly";
         }
         
         // 6. Map Status
         String status = "Pending";
         if (payment.getPaymentStatus() == PaymentStatus.SUCCESS) status = "Paid";
         else if (payment.getPaymentStatus() == PaymentStatus.FAILED) status = "Failed";
         
         // 7. Build Invoice ID
         String invoice = payment.getPaymentStatus() == PaymentStatus.SUCCESS 
                 ? "INV-" + payment.getPaymentId() 
                 : "";
                 
         BillingHistoryResponse row = BillingHistoryResponse.builder()
                 .date(payment.getPaymentDate() != null ? payment.getPaymentDate() : payment.getCreatedOn())
                 .transaction(transactionType)
                 .packageName(packageName)
                 .usage(usage)
                 .billingCycle(billingCycle)
                 .amount(payment.getAmount())
                 .status(status)
                 .invoice(invoice)
                 .build();
                 
         history.add(row);
     }
     
     // Reverse back to return newest first
     java.util.Collections.reverse(history);
     
     return new SingleResponse<>(history, CustomStatus.SUCCESS);
 }
    
}
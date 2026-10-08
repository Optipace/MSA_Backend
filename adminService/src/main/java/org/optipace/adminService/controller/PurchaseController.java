package org.optipace.adminService.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.optipace.adminService.dto.request.PaymentCallbackRequestDto;
import org.optipace.adminService.dto.request.PurchaseRequestDto;
import org.optipace.adminService.dto.response.PurchaseResponse;
import org.optipace.adminService.dto.response.SingleResponse;
import org.optipace.adminService.service.PurchaseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/purchase")
@RequiredArgsConstructor
public class PurchaseController {
    
    private final PurchaseService purchaseService;
    
    /**
     * FACTORY_ADMIN buys a package
     */
    @PostMapping
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<SingleResponse<PurchaseResponse>> buyPackage(
            @Valid @RequestBody PurchaseRequestDto request,
            @RequestHeader("X-User-Id") String userId) {
        
        log.info("REST request: FACTORY_ADMIN {} buying package", userId);
        
        return new ResponseEntity<>(
            purchaseService.buyPackage(request, userId),
            HttpStatus.CREATED
        );
    }
    
    /**
     * Payment gateway callback (usually public)
     */
    @PostMapping("/callback")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<SingleResponse<PurchaseResponse>> handleCallback(
            @RequestBody PaymentCallbackRequestDto callback) {
        
        log.info("REST callback — txn: {}", callback.getTransactionId());
        return ResponseEntity.ok(purchaseService.handleCallback(callback));
    }
}
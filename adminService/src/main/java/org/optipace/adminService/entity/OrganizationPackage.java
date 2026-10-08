package org.optipace.adminService.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.optipace.adminService.enums.Currency;
import org.optipace.adminService.enums.PaymentStatus;
import org.optipace.adminService.enums.SubscriptionStatus;

@Entity
@Table(name = "organization_package", schema = "packages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationPackage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "organization_package_id")
    private Long organizationPackageId;
    
    // ⭐ CHANGED: Package → SubscriptionPackage
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "package_id", nullable = false)
    private SubscriptionPackage packageEntity;
    
    @Column(name = "organization_id", nullable = false)
    private Long organizationId;
    
    @Column(name = "factory_id", nullable = false)
    private Long factoryId;
    
    @Column(name = "purchased_by", nullable = false)
    private Long purchasedBy;
    
    @Column(name = "purchase_date", nullable = false)
    private LocalDateTime purchaseDate;
    
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    
    @Column(name = "package_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal packagePrice;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false, length = 3)
    private Currency currency;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_status", nullable = false)
    private SubscriptionStatus subscriptionStatus;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;
    
    @Column(name = "transaction_id", length = 150)
    private String transactionId;
    
    @Column(name = "remarks")
    private String remarks;
    
    @Column(name = "created_by", nullable = false)
    private Long createdBy;
    
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;
    
    @Column(name = "updated_by")
    private Long updatedBy;
    
    @Column(name = "updated_on")
    private LocalDateTime updatedOn;
    
    @PrePersist
    public void prePersist() {
        createdOn = LocalDateTime.now();
        purchaseDate = LocalDateTime.now();
        if (currency == null) currency = Currency.INR;
        if (paymentStatus == null) paymentStatus = PaymentStatus.PENDING;
        if (subscriptionStatus == null) subscriptionStatus = SubscriptionStatus.ACTIVE;
    }
    
    @PreUpdate
    public void preUpdate() {
        updatedOn = LocalDateTime.now();
    }
}
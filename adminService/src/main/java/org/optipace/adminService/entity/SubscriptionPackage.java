package org.optipace.adminService.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "\"package\"",              // ← escape reserved word
    schema = "packages"
)
public class SubscriptionPackage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "package_id")
    private Long packageId;
    
    @Column(name = "package_name", length = 100, nullable = false)
    private String packageName;
    
    @Column(name = "usage_description", columnDefinition = "text")
    private String usageDescription;
    
    @Column(name = "price", precision = 12, scale = 2, nullable = false)
    private BigDecimal price;
    
    @Column(name = "currency", length = 3, nullable = false)
    private String currency = "INR";
    
    @Column(name = "number_of_days", nullable = false)
    private Integer numberOfDays;
    
    @Column(name = "usage")            // ← reserved word, Hibernate escapes
    private Long usage;
    
    @Column(name = "record_status", nullable = false, columnDefinition = "char(1)")
    private Character recordStatus = 'A';
    
    @Column(name = "created_by")
    private Long createdBy;
    
    @CreationTimestamp
    @Column(name = "created_on", nullable = false, updatable = false)
    private OffsetDateTime createdOn;
    
    @Column(name = "updated_by")
    private Long updatedBy;
    
    @UpdateTimestamp
    @Column(name = "updated_on")
    private OffsetDateTime updatedOn;
}
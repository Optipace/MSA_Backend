package org.optipace.garmentService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "product_category",
    schema = "garment",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_product_category", columnNames = {"category_code"})
    }
)
public class ProductCategory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_category_id")
    private Long productCategoryId;
    
    // ===== CORE FIELDS =====
    @Column(name = "category_code", length = 20, nullable = false)
    private String categoryCode;
    
    @Column(name = "category_name", length = 100, nullable = false)
    private String categoryName;
    
    @Column(name = "description", columnDefinition = "text")
    private String description;
    
    @Column(name = "display_order")
    private Integer displayOrder = 1;
    
    // ===== AUDIT FIELDS =====
    @Column(name = "created_by")
    private Long createdBy;
    
    @CreationTimestamp
    @Column(name = "created_on", updatable = false)
    private OffsetDateTime createdOn;
    
    @Column(name = "updated_by")
    private Long updatedBy;
    
    @UpdateTimestamp
    @Column(name = "updated_on")
    private OffsetDateTime updatedOn;
    
    @Version
    @Column(name = "version_no")
    private Integer versionNo = 1;
    
    @Column(name = "remarks", columnDefinition = "text")
    private String remarks;
    
    // ⚠️ Must be char(1) — NOT varchar(1)
    @Column(name = "record_status", columnDefinition = "char(1)")
    //private String recordStatus = "A";
    private Character recordStatus = 'A';

}
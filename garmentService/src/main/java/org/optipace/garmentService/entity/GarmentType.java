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
    name = "garment_type",
    schema = "garment",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_garment_type", columnNames = {"garment_code"})
    }
)
public class GarmentType {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "garment_type_id")
    private Long garmentTypeId;
    
    // ===== FK: product_category =====
    @Column(name = "product_category_id", nullable = false)
    private Long productCategoryId;
    
    // ===== CORE FIELDS =====
    @Column(name = "garment_code", length = 30, nullable = false)
    private String garmentCode;
    
    @Column(name = "garment_name", length = 100, nullable = false)
    private String garmentName;
    
    @Column(name = "description", columnDefinition = "text")
    private String description;
    
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
   // private String recordStatus = "A";
    private Character recordStatus = 'A';

}
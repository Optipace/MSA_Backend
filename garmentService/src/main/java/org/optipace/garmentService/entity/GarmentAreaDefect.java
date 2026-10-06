package org.optipace.garmentService.entity;

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
    name = "garment_area_defect",
    schema = "garment",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_garment_area_defect",
            columnNames = {"garment_area_id", "defect_id"}
        )
    },
    indexes = {
        @Index(name = "idx_gad_area", columnList = "garment_area_id"),
        @Index(name = "idx_gad_defect", columnList = "defect_id"),
        @Index(name = "idx_gad_severity", columnList = "severity_level_id")
    }
)
public class GarmentAreaDefect {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "garment_area_defect_id")
    private Long garmentAreaDefectId;
    
    // ===== FK: garment_area =====
    @Column(name = "garment_area_id", nullable = false)
    private Long garmentAreaId;
    
    // ===== FK: defect_master =====
    @Column(name = "defect_id", nullable = false)
    private Long defectId;
    
    // ===== FK: severity_level (cross-schema → config) =====
    @Column(name = "severity_level_id", nullable = false)
    private Long severityLevelId;
    
    // ===== BUSINESS FIELDS =====
    @Column(name = "max_deduction_marks", precision = 6, scale = 2)
    private BigDecimal maxDeductionMarks = BigDecimal.ONE;
    
    @Column(name = "is_mandatory")
    private Boolean isMandatory = false;
    
    @Column(name = "ai_confidence_threshold", precision = 5, scale = 2)
    private BigDecimal aiConfidenceThreshold;
    
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
    
    // ⚠️ Must be char(1) to match DB — NOT varchar(1)
   // @Column(name = "record_status", columnDefinition = "char(1)")
    @Column(name = "record_status", length = 1)
    //private Character recordStatus;
    private char recordStatus = 'A';

}
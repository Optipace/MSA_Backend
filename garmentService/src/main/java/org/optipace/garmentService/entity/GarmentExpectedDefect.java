package org.optipace.garmentService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "garment_expected_defect",
    schema = "garment",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_garment_expected_defect",
            columnNames = {"garment_instance_id", "garment_area_id", "defect_id"}
        )
    },
    indexes = {
        @Index(name = "idx_expected_defect_garment", columnList = "garment_instance_id"),
        @Index(name = "idx_expected_defect_garment_area", columnList = "garment_instance_id, garment_area_id")
    }
)
public class GarmentExpectedDefect {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "garment_expected_defect_id", nullable = false, updatable = false)
    private UUID garmentExpectedDefectId;
    
    // ===== FK: garment_instance =====
    @Column(name = "garment_instance_id", nullable = false)
    private UUID garmentInstanceId;
    
    // ===== FK: garment_area =====
    @Column(name = "garment_area_id", nullable = false)
    private Long garmentAreaId;
    
    // ===== FK: defect_master =====
    @Column(name = "defect_id", nullable = false)
    private Long defectId;
    
    // ===== FK: severity_level (config schema) =====
    @Column(name = "severity_level_id", nullable = false)
    private Long severityLevelId;
    
    // ===== AUDIT =====
    @CreationTimestamp
    @Column(name = "created_on", nullable = false, updatable = false)
    private OffsetDateTime createdOn;
}
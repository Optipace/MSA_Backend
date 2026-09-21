package org.optipace.garmentService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "defect_master",
    schema = "garment",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_defect", columnNames = {"defect_code"})
    }
)
public class DefectMaster {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "defect_id")
    private Long defectId;
    
    // ===== FK: defect_category =====
    @Column(name = "defect_category_id", nullable = false)
    private Long defectCategoryId;
    
    @Column(name = "defect_code", length = 30)
    private String defectCode;
    
    @Column(name = "defect_name", length = 200)
    private String defectName;
    
    @Column(name = "description", columnDefinition = "text")
    private String description;
    
    @Column(name = "display_order")
    private Integer displayOrder;
    
    @CreationTimestamp
    @Column(name = "created_on", updatable = false)
    private OffsetDateTime createdOn;
}
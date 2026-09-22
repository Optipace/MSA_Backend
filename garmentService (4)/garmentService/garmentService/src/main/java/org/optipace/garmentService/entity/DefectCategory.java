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
    name = "defect_category",
    schema = "garment",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_defect_category", columnNames = {"category_code"})
    }
)
public class DefectCategory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "defect_category_id")
    private Long defectCategoryId;
    
    @Column(name = "category_code", length = 30)
    private String categoryCode;
    
    @Column(name = "category_name", length = 100)
    private String categoryName;
    
    @Column(name = "description", columnDefinition = "text")
    private String description;
    
    @Column(name = "display_order")
    private Integer displayOrder;
    
    @CreationTimestamp
    @Column(name = "created_on", updatable = false)
    private OffsetDateTime createdOn;
}
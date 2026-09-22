package org.optipace.garmentService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "garment_area",
    schema = "garment",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_garment_area",
            columnNames = {"garment_type_id", "area_code"}
        )
    },
    indexes = {
        @Index(name = "idx_garment_area_type", columnList = "garment_type_id")
    }
)
public class GarmentArea {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "garment_area_id")
    private Long garmentAreaId;																																													
    
    // ===== FK: garment_type =====
    @Column(name = "garment_type_id", nullable = false)
    private Long garmentTypeId;
    
    @Column(name = "area_code", length = 30, nullable = false)
    private String areaCode;
    
    @Column(name = "area_name", length = 100, nullable = false)
    private String areaName;
    
    @Column(name = "display_order")
    private Integer displayOrder;
    
    @Column(name = "x_coordinate", precision = 10, scale = 2)
    private BigDecimal xCoordinate;
    
    @Column(name = "y_coordinate", precision = 10, scale = 2)
    private BigDecimal yCoordinate;
    
    @Column(name = "width", precision = 10, scale = 2)
    private BigDecimal width;
    
    @Column(name = "height", precision = 10, scale = 2)
    private BigDecimal height;
    
    @CreationTimestamp
    @Column(name = "created_on", updatable = false)
    private OffsetDateTime createdOn;
}
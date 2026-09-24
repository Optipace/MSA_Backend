package org.optipace.garmentService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "garment_lot",
    schema = "garment",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_garment_lot", columnNames = {"lot_number"})
    }
)
public class GarmentLot {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "garment_lot_id", nullable = false, updatable = false)
    private UUID garmentLotId;
    
    // ===== CORE FIELDS =====
    @Column(name = "lot_number", length = 50, nullable = false)
    private String lotNumber;
    
    // ===== FK: garment_type =====
    @Column(name = "garment_type_id", nullable = false)
    private Long garmentTypeId;
    
    // ===== FK: master.factory (cross-schema) =====
    @Column(name = "factory_id", nullable = false)
    private Long factoryId;
    
    @Column(name = "production_date")
    private LocalDate productionDate;
    
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    
    @Column(name = "buyer_name", length = 200)
    private String buyerName;
    
    @Column(name = "purchase_order_no", length = 100)
    private String purchaseOrderNo;
    
    @Column(name = "color", length = 50)
    private String color;
    
    @Column(name = "size", length = 20)
    private String size;
    
    @Column(name = "remarks", columnDefinition = "text")
    private String remarks;
    
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
    
    // ⚠️ Must be char(1) — NOT varchar(1)
    @Column(name = "record_status", columnDefinition = "char(1)")
    //private String recordStatus = "A";
    private Character recordStatus = 'A';

}
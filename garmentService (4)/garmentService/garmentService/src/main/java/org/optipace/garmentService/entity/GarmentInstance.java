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
    name = "garment_instance",
    schema = "garment",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_serial", columnNames = {"serial_number"}),
        @UniqueConstraint(name = "uk_qr",     columnNames = {"qr_code"}),
        @UniqueConstraint(name = "uk_barcode", columnNames = {"barcode"})
    },
    indexes = {
        @Index(name = "idx_instance_lot", columnList = "garment_lot_id")
    }
)
public class GarmentInstance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "garment_instance_id", nullable = false, updatable = false)
    private UUID garmentInstanceId;
    
    // ===== CORE FIELDS =====
    @Column(name = "serial_number", length = 50, nullable = false)
    private String serialNumber;
    
    @Column(name = "qr_code", length = 100)
    private String qrCode;
    
    @Column(name = "barcode", length = 100)
    private String barcode;
    
    @Column(name = "current_status", length = 20, nullable = false)
    private String currentStatus = "AVAILABLE";
    
    @Column(name = "current_location", length = 100)
    private String currentLocation;
    
    @Column(name = "manufactured_on")
    private LocalDate manufacturedOn;
    
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
    private String recordStatus = "A";
    
    // ===== FK: garment_lot =====
    @Column(name = "garment_lot_id", nullable = false)
    private UUID garmentLotId;
}
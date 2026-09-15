package com.msa.msa.garment.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "garment_instance", schema = "garment")
@Getter
@Setter
public class GarmentInstance {

    @Id
    @GeneratedValue
    @Column(name = "garment_instance_id")
    private UUID garmentInstanceId;

    @Column(name = "serial_number", nullable = false, length = 50)
    private String serialNumber;

    @Column(name = "qr_code", length = 100)
    private String qrCode;

    @Column(name = "barcode", length = 100)
    private String barcode;

    @Column(name = "current_status", nullable = false, length = 20)
    private String currentStatus;

    @Column(name = "current_location", length = 100)
    private String currentLocation;

    @Column(name = "manufactured_on")
    private LocalDate manufacturedOn;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_on")
    private OffsetDateTime createdOn;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "updated_on")
    private OffsetDateTime updatedOn;

    @Column(name = "version_no")
    private Integer versionNo;

    @Column(name = "remarks")
    private String remarks;

    @JdbcTypeCode(Types.CHAR)
    @Column(name = "record_status")
    private String recordStatus;

    @Column(name = "garment_lot_id", nullable = false)
    private UUID garmentLotId;
}
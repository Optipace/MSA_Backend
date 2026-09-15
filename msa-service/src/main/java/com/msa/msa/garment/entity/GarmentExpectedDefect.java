package com.msa.msa.garment.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "garment_expected_defect",
        schema = "garment",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_garment_expected_defect",
                        columnNames = {
                                "garment_instance_id",
                                "garment_area_id",
                                "defect_id"
                        }
                )
        }
)
@Getter
@Setter
public class GarmentExpectedDefect {

    @Id
    @GeneratedValue
    @Column(name = "garment_expected_defect_id")
    private UUID garmentExpectedDefectId;

    @Column(name = "garment_instance_id", nullable = false)
    private UUID garmentInstanceId;

    @Column(name = "garment_area_id", nullable = false)
    private Long garmentAreaId;

    @Column(name = "defect_id", nullable = false)
    private Long defectId;

    @Column(name = "severity_level_id", nullable = false)
    private Long severityLevelId;

    @Column(name = "created_on", nullable = false)
    private OffsetDateTime createdOn;
}
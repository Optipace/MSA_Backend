package org.optipace.garmentService.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "garment_defect_area_submission",
    schema = "garment"
)
@Getter
@Setter
@NoArgsConstructor
public class GarmentDefectAreaSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "submission_detail_id")
    private Long submissionDetailId;

    @Column(name = "batch_id", nullable = false)
    private Long batchId;

    @Column(name = "garment_id", nullable = false, length = 50)
    private String garmentId;

    @Column(name = "defect_id")
    private Long defectId;

    @Column(name = "area_id")
    private Long areaId;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;

    @PrePersist
    public void prePersist() {
        if (createdOn == null) {
            createdOn = LocalDateTime.now();
        }
    }
}
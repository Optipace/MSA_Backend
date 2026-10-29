
package com.msa.msa.garment.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "garment_identification_response", schema = "assessment")
@Getter
@Setter
public class GarmentIdentificationResponse {

    @Id
    @GeneratedValue
    @Column(name = "garment_identification_response_id")
    private UUID garmentIdentificationResponseId;

    @Column(name = "assessment_response_id", nullable = false)
    private UUID assessmentResponseId;

    @Column(name = "batch_id", nullable = false)
    private Long batchId;

    @Column(name = "garment_id", nullable = false, length = 100)
    private String garmentId;

    @Column(name = "selected_defect_id", nullable = false)
    private Long selectedDefectId;

    @Column(name = "selected_garment_area_id", nullable = false)
    private Long selectedGarmentAreaId;

    @Column(name = "expected_submission_detail_id")
    private Long expectedSubmissionDetailId;

    @Column(name = "defect_correct")
    private Boolean defectCorrect;

    @Column(name = "area_correct")
    private Boolean areaCorrect;

    @Column(name = "defect_score", precision = 6, scale = 2)
    private BigDecimal defectScore;

    @Column(name = "area_score", precision = 6, scale = 2)
    private BigDecimal areaScore;

    @Column(name = "created_on", nullable = false)
    private OffsetDateTime createdOn;
}

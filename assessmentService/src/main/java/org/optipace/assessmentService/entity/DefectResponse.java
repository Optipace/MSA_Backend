package org.optipace.assessmentService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "defect_response"
        , schema = "assessment"
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DefectResponse {

    @Id
    @Column(name = "defect_response_id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID defectResponseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_response_id", nullable = false)
    private AssessmentResponse assessmentResponse;

    @Column(name = "garment_instance_area_id", nullable = false)
    private UUID garmentInstanceAreaId;

    @Column(name = "expected_defect_id")
    private Long expectedDefectId;

    @Column(name = "selected_defect_id")
    private Long selectedDefectId;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @Column(name = "score", precision = 6, scale = 2)
    private BigDecimal score;

    @Column(name = "remarks")
    private String remarks;

    @CreationTimestamp
    @Column(name = "created_on", length = 35)
    private LocalDateTime createdOn;

}



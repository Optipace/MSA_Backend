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
@Table(name = "area_response"
        , schema = "assessment"
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AreaResponse {

    @Id
    @Column(name = "area_response_id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID areaResponseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_response_id", nullable = false)
    private AssessmentResponse assessmentResponse;

    @Column(name = "garment_area_id", nullable = false)
    private Long garmentAreaId;

    @Column(name = "selected")
    private Boolean selected;

    @Column(name = "score", precision = 6, scale = 2)
    private BigDecimal score;

    @CreationTimestamp
    @Column(name = "created_on", length = 35)
    private LocalDateTime createdOn;

}



package org.optipace.assessmentService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "final_score"
        , schema = "assessment"
        , uniqueConstraints = @UniqueConstraint(columnNames = "assessment_session_id")
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FinalScore {

    @Id
    @Column(name = "final_score_id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID finalScoreId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_session_id", unique = true, nullable = false)
    private AssessmentSession assessmentSession;

    @Column(name = "total_score", precision = 8, scale = 2)
    private BigDecimal totalScore;

    @Column(name = "percentage", precision = 5, scale = 2)
    private BigDecimal percentage;

    @Column(name = "competency_rating_id")
    private Long competencyRatingId;

    @Column(name = "risk_level_id")
    private Long riskLevelId;

    @Column(name = "overall_result", length = 30)
    private String overallResult;

    @Column(name = "calculated_on", length = 35)
    private LocalDateTime calculatedOn;

}



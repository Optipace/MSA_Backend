package com.msa.msa.scoring.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "final_score",
        schema = "assessment",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_final_score_session",
                        columnNames = {"assessment_session_id"}
                )
        }
)
@Getter
@Setter
public class FinalScore {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "final_score_id")
    private UUID finalScoreId;

    @Column(name = "assessment_session_id", nullable = false)
    private UUID assessmentSessionId;

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

    @Column(name = "calculated_on")
    private OffsetDateTime calculatedOn;
}
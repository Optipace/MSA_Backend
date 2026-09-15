package com.msa.msa.scoring.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "module_score", schema = "assessment")
@Getter
@Setter
public class ModuleScore {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "module_score_id")
    private UUID moduleScoreId;

    @Column(name = "assessment_module_instance_id", nullable = false)
    private UUID assessmentModuleInstanceId;

    @Column(name = "maximum_score", precision = 8, scale = 2)
    private BigDecimal maximumScore;

    @Column(name = "obtained_score", precision = 8, scale = 2)
    private BigDecimal obtainedScore;

    @Column(name = "percentage", precision = 5, scale = 2)
    private BigDecimal percentage;

    @Column(name = "competency_rating_id")
    private Long competencyRatingId;

    @Column(name = "calculated_on")
    private OffsetDateTime calculatedOn;
}
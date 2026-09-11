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
@Table(name = "module_score"
        , schema = "assessment"
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ModuleScore {

    @Id
    @Column(name = "module_score_id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID moduleScoreId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_module_instance_id", nullable = false)
    private AssessmentModuleInstance assessmentModuleInstance;

    @Column(name = "maximum_score", precision = 8, scale = 2)
    private BigDecimal maximumScore;

    @Column(name = "obtained_score", precision = 8, scale = 2)
    private BigDecimal obtainedScore;

    @Column(name = "percentage", precision = 5, scale = 2)
    private BigDecimal percentage;

    @Column(name = "competency_rating_id")
    private Long competencyRatingId;

    @Column(name = "calculated_on", length = 35)
    private LocalDateTime calculatedOn;

}



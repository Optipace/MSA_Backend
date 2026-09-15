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
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "assessment_module_instance"
        , schema = "assessment"
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AssessmentModuleInstance {

    @Id
    @Column(name = "assessment_module_instance_id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID assessmentModuleInstanceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_attempt_id", nullable = false)
    private AssessmentAttempt assessmentAttempt;

    @Column(name = "assessment_module_id", nullable = false)
    private Long assessmentModuleId;

    @Column(name = "sequence_no")
    private Integer sequenceNo;

    @Column(name = "started_on", length = 35)
    private LocalDateTime startedOn;

    @Column(name = "completed_on", length = 35)
    private LocalDateTime completedOn;

    @Column(name = "module_status_id")
    private Long moduleStatusId;

    @Column(name = "obtained_score", precision = 8, scale = 2)
    private BigDecimal obtainedScore;

    @Column(name = "maximum_score", precision = 8, scale = 2)
    private BigDecimal maximumScore;

    @Column(name = "percentage", precision = 5, scale = 2)
    private BigDecimal percentage;

    @CreationTimestamp
    @Column(name = "created_on", length = 35)
    private LocalDateTime createdOn;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "assessmentModuleInstance")
    private Set<AssessmentResponse> assessmentResponses = new HashSet<AssessmentResponse>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "assessmentModuleInstance")
    private Set<ModuleScore> moduleScores = new HashSet<ModuleScore>(0);

}



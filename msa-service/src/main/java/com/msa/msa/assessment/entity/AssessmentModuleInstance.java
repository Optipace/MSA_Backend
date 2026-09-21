package com.msa.msa.assessment.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;

import java.math.BigDecimal;
import java.sql.Types;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "assessment_module_instance", schema = "assessment")
@Getter
@Setter
public class AssessmentModuleInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "assessment_module_instance_id")
    private UUID assessmentModuleInstanceId;

    @Column(name = "assessment_module_id", nullable = false)
    private Long assessmentModuleId;

    @Column(name = "sequence_no", nullable = false)
    private Integer sequenceNo;

    @Column(name = "started_on")
    private OffsetDateTime startedOn;

    @Column(name = "completed_on")
    private OffsetDateTime completedOn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "module_status_id",
        nullable = false
    )
    private ModuleStatus moduleStatus;

    @Column(name = "obtained_score")
    private BigDecimal obtainedScore;

    @Column(name = "maximum_score")
    private BigDecimal maximumScore;

    @Column(name = "percentage")
    private BigDecimal percentage;

    @Column(name = "created_on", nullable = false)
    private OffsetDateTime createdOn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "assessment_attempt_id",
        nullable = false
    )
    private AssessmentAttempt assessmentAttempt;
}
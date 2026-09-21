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
@Table(name = "assessment_attempt", schema = "assessment")
@Getter
@Setter
public class AssessmentAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "assessment_attempt_id")
    private UUID assessmentAttemptId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "assessment_session_id",
        nullable = false
    )
    private AssessmentSession assessmentSession;

    @Column(name = "attempt_no", nullable = false)
    private Integer attemptNo;

    @Column(name = "started_on")
    private OffsetDateTime startedOn;

    @Column(name = "completed_on")
    private OffsetDateTime completedOn;

    @Column(name = "total_score")
    private BigDecimal totalScore;

    @Column(name = "percentage")
    private BigDecimal percentage;

    @Column(name = "result")
    private String result;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "created_on", nullable = false)
    private OffsetDateTime createdOn;

    @Column(name = "updated_on")
    private OffsetDateTime updatedOn;

    @Column(name = "version_no")
    private Integer versionNo;

    @JdbcTypeCode(Types.CHAR)
    @Column(name = "record_status")
    private String recordStatus;
}
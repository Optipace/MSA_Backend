package com.msa.msa.assessment.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import java.sql.Types;

@Entity
@Table(
        name = "assessment_session",
        schema = "assessment"
)
@Getter
@Setter
public class AssessmentSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "assessment_session_id")
    private UUID assessmentSessionId;

    @Column(name = "assessment_no")
    private String assessmentNo;

    @Column(name = "employee_id")
    private Long employeeId;

    @Column(name = "garment_instance_id")
    private UUID garmentInstanceId;

    @Column(name = "assessment_template_id")
    private Long assessmentTemplateId;

    @Column(name = "assessment_cycle_id")
    private Long assessmentCycleId;

    @Column(name = "assigned_by")
    private Long assignedBy;

    @Column(name = "assigned_on")
    private OffsetDateTime assignedOn;

    @Column(name = "scheduled_on")
    private OffsetDateTime scheduledOn;

    @Column(name = "started_on")
    private OffsetDateTime startedOn;

    @Column(name = "completed_on")
    private OffsetDateTime completedOn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "assessment_status_id",
            nullable = false
    )
    private AssessmentStatus assessmentStatus;

    @Column(name = "total_score")
    private BigDecimal totalScore;

    @Column(name = "percentage")
    private BigDecimal percentage;

    @Column(name = "competency_rating_id")
    private Long competencyRatingId;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_on")
    private OffsetDateTime createdOn;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "updated_on")
    private OffsetDateTime updatedOn;

    @Column(name = "version_no")
    private Integer versionNo;

    @JdbcTypeCode(Types.CHAR)
    @Column(name = "record_status")
    private String recordStatus;
}
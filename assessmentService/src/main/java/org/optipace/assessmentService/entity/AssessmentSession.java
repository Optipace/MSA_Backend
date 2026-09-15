package org.optipace.assessmentService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "assessment_session"
        , schema = "assessment"
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AssessmentSession {

    @Id
    @Column(name = "assessment_session_id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID assessmentSessionId;

    @Column(name = "assessment_no", nullable = false, length = 50)
    private String assessmentNo;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(name = "garment_instance_id")
    private UUID garmentInstanceId;

    @Column(name = "assessment_template_id", nullable = false)
    private Long assessmentTemplateId;

    @Column(name = "assessment_cycle_id", nullable = false)
    private Long assessmentCycleId;

    @Column(name = "assigned_by", nullable = false)
    private Long assignedBy;

    @Column(name = "assigned_on", nullable = false, length = 35)
    private LocalDateTime assignedOn;

    @Column(name = "scheduled_on", length = 35)
    private LocalDateTime scheduledOn;

    @Column(name = "started_on", length = 35)
    private LocalDateTime startedOn;

    @Column(name = "completed_on", length = 35)
    private LocalDateTime completedOn;

    @Column(name = "assessment_status_id", nullable = false)
    private Long assessmentStatusId;

    @Column(name = "total_score", precision = 8, scale = 2)
    private BigDecimal totalScore;

    @Column(name = "percentage", precision = 5, scale = 2)
    private BigDecimal percentage;

    @Column(name = "competency_rating_id")
    private Long competencyRatingId;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "created_by")
    private Long createdBy;

    @CreationTimestamp
    @Column(name = "created_on", length = 35)
    private LocalDateTime createdOn;

    @Column(name = "updated_by")
    private Long updatedBy;

    @UpdateTimestamp
    @Column(name = "updated_on", length = 35)
    private LocalDateTime updatedOn;

    @Column(name = "version_no")
    private Integer versionNo;

    @Column(name = "record_status", length = 1)
    private Character recordStatus;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "assessmentSession")
    private Set<EmployeeSnapshot> employeeSnapshots = new HashSet<EmployeeSnapshot>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "assessmentSession")
    private Set<AssessmentAttempt> assessmentAttempts = new HashSet<AssessmentAttempt>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "assessmentSession")
    private Set<FinalScore> finalScores = new HashSet<FinalScore>(0);

}



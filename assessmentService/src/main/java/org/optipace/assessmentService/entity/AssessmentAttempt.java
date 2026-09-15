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
@Table(name = "assessment_attempt"
        , schema = "assessment"
        , uniqueConstraints = @UniqueConstraint(columnNames = {"assessment_session_id", "attempt_no"})
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AssessmentAttempt {

    @Id
    @Column(name = "assessment_attempt_id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID assessmentAttemptId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_session_id", nullable = false)
    private AssessmentSession assessmentSession;

    @Column(name = "attempt_no", nullable = false)
    private int attemptNo;

    @Column(name = "started_on", length = 35)
    private LocalDateTime startedOn;

    @Column(name = "completed_on", length = 35)
    private LocalDateTime completedOn;

    @Column(name = "total_score", precision = 8, scale = 2)
    private BigDecimal totalScore;

    @Column(name = "percentage", precision = 5, scale = 2)
    private BigDecimal percentage;

    @Column(name = "result", length = 20)
    private String result;

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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "assessmentAttempt")
    private Set<AssessmentModuleInstance> assessmentModuleInstances = new HashSet<AssessmentModuleInstance>(0);

}



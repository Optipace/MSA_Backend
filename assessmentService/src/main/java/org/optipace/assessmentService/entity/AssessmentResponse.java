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
@Table(name = "assessment_response"
        , schema = "assessment"
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AssessmentResponse {

    @Id
    @Column(name = "assessment_response_id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID assessmentResponseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_module_instance_id", nullable = false)
    private AssessmentModuleInstance assessmentModuleInstance;

    @Column(name = "response_status_id", nullable = false)
    private Long responseStatusId;

    @Column(name = "started_on", length = 35)
    private LocalDateTime startedOn;

    @Column(name = "completed_on", length = 35)
    private LocalDateTime completedOn;

    @Column(name = "time_taken_seconds")
    private Integer timeTakenSeconds;

    @Column(name = "total_questions")
    private Integer totalQuestions;

    @Column(name = "answered_questions")
    private Integer answeredQuestions;

    @Column(name = "unanswered_questions")
    private Integer unansweredQuestions;

    @Column(name = "raw_score", precision = 8, scale = 2)
    private BigDecimal rawScore;

    @Column(name = "percentage", precision = 5, scale = 2)
    private BigDecimal percentage;

    @Column(name = "remarks")
    private String remarks;

    @CreationTimestamp
    @Column(name = "created_on", length = 35)
    private LocalDateTime createdOn;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "assessmentResponse")
    private Set<DefectResponse> defectResponses = new HashSet<DefectResponse>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "assessmentResponse")
    private Set<AreaResponse> areaResponses = new HashSet<AreaResponse>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "assessmentResponse")
    private Set<ColorResponse> colorResponses = new HashSet<ColorResponse>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "assessmentResponse")
    private Set<ImageAnnotation> imageAnnotations = new HashSet<ImageAnnotation>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "assessmentResponse")
    private Set<QuestionResponse> questionResponses = new HashSet<QuestionResponse>(0);

}



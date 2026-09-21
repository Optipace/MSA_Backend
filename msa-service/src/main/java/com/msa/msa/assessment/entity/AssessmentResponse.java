package com.msa.msa.assessment.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "assessment_response", schema = "assessment")
@Getter
@Setter
public class AssessmentResponse {

    @Id
    @GeneratedValue
    @Column(name = "assessment_response_id")
    private UUID assessmentResponseId;

    @Column(name = "assessment_module_instance_id", nullable = false)
    private UUID assessmentModuleInstanceId;

    @Column(name = "response_status_id")
    private Long responseStatusId;

    @Column(name = "started_on")
    private OffsetDateTime startedOn;

    @Column(name = "completed_on")
    private OffsetDateTime completedOn;

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

    @Column(name = "created_on")
    private OffsetDateTime createdOn;
}
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
import java.util.UUID;

@Entity
@Table(name = "question_response"
        , schema = "assessment"
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class QuestionResponse {

    @Id
    @Column(name = "question_response_id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID questionResponseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_response_id", nullable = false)
    private AssessmentResponse assessmentResponse;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "selected_option_id")
    private Long selectedOptionId;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @Column(name = "marks_obtained", precision = 6, scale = 2)
    private BigDecimal marksObtained;

    @Column(name = "response_time_seconds")
    private Integer responseTimeSeconds;

    @CreationTimestamp
    @Column(name = "created_on", length = 35)
    private LocalDateTime createdOn;

}



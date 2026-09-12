package org.optipace.assessmentService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "color_response"
        , schema = "assessment"
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ColorResponse {

    @Id
    @Column(name = "color_response_id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID colorResponseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_response_id", nullable = false)
    private AssessmentResponse assessmentResponse;

    @Column(name = "expected_color", length = 50)
    private String expectedColor;

    @Column(name = "selected_color", length = 50)
    private String selectedColor;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @Column(name = "response_time_seconds")
    private Integer responseTimeSeconds;

    @CreationTimestamp
    @Column(name = "created_on", length = 35)
    private LocalDateTime createdOn;

}



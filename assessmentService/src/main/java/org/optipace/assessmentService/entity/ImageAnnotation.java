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
@Table(name = "image_annotation"
        , schema = "assessment"
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ImageAnnotation {

    @Id
    @Column(name = "image_annotation_id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID imageAnnotationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_response_id", nullable = false)
    private AssessmentResponse assessmentResponse;

    @Column(name = "garment_instance_area_id")
    private UUID garmentInstanceAreaId;

    @Column(name = "x_coordinate", precision = 10, scale = 2)
    private BigDecimal XCoordinate;

    @Column(name = "y_coordinate", precision = 10, scale = 2)
    private BigDecimal YCoordinate;

    @Column(name = "width", precision = 10, scale = 2)
    private BigDecimal width;

    @Column(name = "height", precision = 10, scale = 2)
    private BigDecimal height;

    @Column(name = "annotation_text")
    private String annotationText;

    @Column(name = "ai_detected")
    private Boolean aiDetected;

    @Column(name = "confidence_percentage", precision = 5, scale = 2)
    private BigDecimal confidencePercentage;

    @CreationTimestamp
    @Column(name = "created_on", length = 35)
    private LocalDateTime createdOn;

}



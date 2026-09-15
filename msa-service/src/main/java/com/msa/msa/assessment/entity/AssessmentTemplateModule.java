package com.msa.msa.assessment.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "assessment_template_module", schema = "config")
@Getter
@Setter
public class AssessmentTemplateModule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assessment_template_module_id")
    private Long assessmentTemplateModuleId;

    @Column(name = "assessment_template_id", nullable = false)
    private Long assessmentTemplateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "assessment_module_id",
        nullable = false
    )
    private AssessmentModule assessmentModule;

    @Column(name = "sequence_no", nullable = false)
    private Integer sequenceNo;

    @Column(name = "is_mandatory")
    private Boolean isMandatory;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "created_on")
    private OffsetDateTime createdOn;
}
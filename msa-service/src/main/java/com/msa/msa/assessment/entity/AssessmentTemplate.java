package com.msa.msa.assessment.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;

import java.math.BigDecimal;
import java.sql.Types;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "assessment_template", schema = "config")
@Getter
@Setter
public class AssessmentTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assessment_template_id")
    private Long assessmentTemplateId;

    @Column(name = "template_code", nullable = false, length = 50)
    private String templateCode;

    @Column(name = "template_name", nullable = false, length = 150)
    private String templateName;

    @Column(name = "description")
    private String description;

    @Column(name = "assessment_cycle_id", nullable = false)
    private Long assessmentCycleId;

    @Column(name = "pass_percentage")
    private BigDecimal passPercentage;

    @Column(name = "validity_days")
    private Integer validityDays;

    @Column(name = "version_no")
    private Integer versionNo;

    @Column(name = "effective_from")
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    @Column(name = "is_default")
    private Boolean isDefault;

    @Column(name = "created_on")
    private OffsetDateTime createdOn;

    @Column(name = "updated_on")
    private OffsetDateTime updatedOn;

    @JdbcTypeCode(Types.CHAR)
    @Column(name = "record_status")
    private String recordStatus;

    @Column(name = "question_paper_id")
    private Long questionPaperId;
}
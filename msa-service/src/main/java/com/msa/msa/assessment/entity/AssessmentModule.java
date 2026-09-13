package com.msa.msa.assessment.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;

@Entity
@Table(name = "assessment_module", schema = "config")
@Getter
@Setter
public class AssessmentModule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assessment_module_id")
    private Long assessmentModuleId;

    @Column(name = "module_code", nullable = false, length = 50)
    private String moduleCode;

    @Column(name = "module_name", nullable = false, length = 100)
    private String moduleName;

    @Column(name = "description")
    private String description;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "is_scored")
    private Boolean isScored;

    @Column(name = "is_mandatory")
    private Boolean isMandatory;

    @Column(name = "created_on")
    private java.time.OffsetDateTime createdOn;

    @Column(name = "updated_on")
    private java.time.OffsetDateTime updatedOn;

    @JdbcTypeCode(Types.CHAR)
    @Column(name = "record_status")
    private String recordStatus;
}
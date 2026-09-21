package com.msa.msa.assessment.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "assessment_template_rule", schema = "config")
@Getter
@Setter
public class AssessmentTemplateRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assessment_template_rule_id")
    private Long assessmentTemplateRuleId;

    @Column(name = "assessment_template_id", nullable = false)
    private Long assessmentTemplateId;

    @Column(name = "rule_name", nullable = false, length = 100)
    private String ruleName;

    @Column(name = "rule_value", nullable = false, length = 255)
    private String ruleValue;

    @Column(name = "created_on", nullable = false)
    private OffsetDateTime createdOn;
}
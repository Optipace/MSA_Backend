package com.msa.msa.assessment.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.time.OffsetDateTime;

@Entity
@Table(name = "assessment_status", schema = "assessment")
@Getter
@Setter
public class AssessmentStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assessment_status_id")
    private Long assessmentStatusId;

    @Column(name = "status_code", nullable = false, unique = true, length = 30)
    private String statusCode;

    @Column(name = "status_name", nullable = false, length = 100)
    private String statusName;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "display_order")
    private Integer displayOrder;

    @JdbcTypeCode(Types.CHAR)
    @Column(name = "record_status", nullable = false)
    private String recordStatus;

    @Column(name = "created_on", nullable = false)
    private OffsetDateTime createdOn;
}
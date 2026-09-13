package com.msa.msa.scoring.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.sql.Types;
import org.hibernate.annotations.JdbcTypeCode;
import java.time.OffsetDateTime;

@Entity
@Table(name = "competency_rating", schema = "config")
@Getter
@Setter
public class CompetencyRating {

    @Id
    @Column(name = "competency_rating_id")
    private Long competencyRatingId;

    @Column(name = "rating_code", nullable = false, length = 30)
    private String ratingCode;

    @Column(name = "rating_name", nullable = false, length = 100)
    private String ratingName;

    @Column(name = "color_code", length = 20)
    private String colorCode;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_on")
    private OffsetDateTime createdOn;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "updated_on")
    private OffsetDateTime updatedOn;

    @Column(name = "version_no")
    private Integer versionNo;

    @Column(name = "remarks")
    private String remarks;

    @JdbcTypeCode(Types.CHAR)
    @Column(name = "record_status", length = 1)
    private String recordStatus;
}
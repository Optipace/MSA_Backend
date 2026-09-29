package org.optipace.garmentService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "batch",
    schema = "garment",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_batch_name", columnNames = {"batch_name"})
    }
)
public class Batch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "batch_id")
    private Long batchId;

    @Column(name = "batch_name", length = 50, nullable = false)
    private String batchName;              // "Batch A", "Batch B", ...

    @Column(name = "batch_code", length = 30)
    private String batchCode;              // "A", "B", ... (optional)

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "display_order")
    private Integer displayOrder;

    // ⚠️ Must be char(1) — NOT varchar(1)
    @Column(name = "record_status", columnDefinition = "char(1)")
    private Character recordStatus = 'A';

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @CreationTimestamp
    @Column(name = "created_on", updatable = false)
    private OffsetDateTime createdOn;

    @UpdateTimestamp
    @Column(name = "updated_on")
    private OffsetDateTime updatedOn;
}
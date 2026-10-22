package org.optipace.garmentService.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "garment_batch",
    schema = "garment",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_garment_batch_id_per_batch",
            columnNames = {"batch_id", "garment_id"}
        )
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GarmentBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "garment_batch_id")
    private Long garmentBatchId;

    @Column(name = "batch_id", nullable = false)
    private Long batchId;

    @Column(name = "product_category_id", nullable = false)
    private Long productCategoryId;

    @Column(name = "garment_id", nullable = false, length = 100)
    private String garmentId;

    @Column(name = "garment_name", nullable = false, length = 100)
    private String garmentName;
}
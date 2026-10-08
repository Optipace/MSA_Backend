package org.optipace.adminService.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import org.optipace.adminService.enums.PointTransactionType;

@Entity
@Table(
    name = "point_transaction",
    schema = "packages"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_transaction_id")
    private Long pointTransactionId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "organization_package_id",
        nullable = false
    )
    private OrganizationPackage organizationPackage;

    @Column(name = "organization_id", nullable = false)
    private Long organizationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private PointTransactionType transactionType;

    @Column(name = "points", nullable = false)
    private Long points;

    @Column(name = "reference_type")
    private String referenceType;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(name = "description")
    private String description;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;

    @PrePersist
    public void prePersist() {
        createdOn = LocalDateTime.now();
    }
}
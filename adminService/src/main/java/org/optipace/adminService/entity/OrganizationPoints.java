package org.optipace.adminService.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "organization_points",
    schema = "packages"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationPoints {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "points_id")
    private Long pointsId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "organization_package_id",
        nullable = false,
        unique = true
    )
    private OrganizationPackage organizationPackage;

    @Column(name = "organization_id", nullable = false)
    private Long organizationId;

    @Column(name = "total_points", nullable = false)
    private Long totalPoints;

    @Column(name = "used_points", nullable = false)
    private Long usedPoints;

    @Column(name = "available_points", nullable = false)
    private Long availablePoints;

    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;

    @Column(name = "updated_on")
    private LocalDateTime updatedOn;

    @PrePersist
    public void prePersist() {
        createdOn = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedOn = LocalDateTime.now();
    }
}
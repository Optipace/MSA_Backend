package org.optipace.adminService.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "organization_points", schema = "packages")
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
    
    @Column(name = "organization_package_id", nullable = false)
    private Long organizationPackageId;
    
    @Column(name = "organization_id", nullable = false)
    private Long organizationId;
    
    @Builder.Default
    @Column(name = "total_points", nullable = false)
    private Long totalPoints = 0L;
    
    @Builder.Default
    @Column(name = "used_points", nullable = false)
    private Long usedPoints = 0L;
    
    @Builder.Default
    @Column(name = "available_points", nullable = false)
    private Long availablePoints = 0L;
    
    // ⭐ NEW — cache expiry from organization_package.end_date
    @Column(name = "expiry_date")
    private LocalDate expiryDate;
    
    @Builder.Default
    @Column(name = "is_expired")
    private Boolean isExpired = false;
    
    @CreationTimestamp
    @Column(name = "created_on", nullable = false, updatable = false)
    private LocalDateTime createdOn;
    
    @UpdateTimestamp
    @Column(name = "updated_on")
    private LocalDateTime updatedOn;
}
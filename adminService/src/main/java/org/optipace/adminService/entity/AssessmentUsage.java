package org.optipace.adminService.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "assessment_usage", schema = "assessment")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class AssessmentUsage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usage_id")
    private Long usageId;
    
    @Column(name = "organization_id", nullable = false)
    private Long organizationId;
    
    @Column(name = "factory_id", nullable = false)
    private Long factoryId;
    
    @Column(name = "assessed_employee_id", nullable = false)
    private Long assessedEmployeeId;
    
    @Column(name = "assessor_id", nullable = false)
    private Long assessorId;
    
    @Column(name = "reference_id")
    private Long referenceId;
    
    @Column(name = "module_code", nullable = false, length = 30)
    private String moduleCode;
    
    @Column(name = "points_deducted", nullable = false)
    private Long pointsDeducted;
    
    @Column(name = "status", length = 20, nullable = false)
    private String status = "ASSIGNED";
    
    @Column(name = "score", precision = 5, scale = 2)
    private BigDecimal score;
    
    @CreationTimestamp
    @Column(name = "assigned_at", updatable = false)
    private OffsetDateTime assignedAt;
    
    @Column(name = "started_at")
    private OffsetDateTime startedAt;
    
    @Column(name = "completed_at")
    private OffsetDateTime completedAt;
}
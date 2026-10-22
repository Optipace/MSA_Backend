package org.optipace.adminService.entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "activity_log", schema = "master")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activity_id")
    private Long activityId;

    @Column(name = "organization_id")
    private Long organizationId;

    @Column(name = "activity_type", length = 50, nullable = false)
    private String activityType;

    @Column(name = "description", columnDefinition = "text", nullable = false)
    private String description;

    @Column(name = "created_by")
    private Long createdBy;

    @CreationTimestamp
    @Column(name = "created_on", nullable = false, updatable = false)
    private OffsetDateTime createdOn;
}
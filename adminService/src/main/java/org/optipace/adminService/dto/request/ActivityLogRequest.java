package org.optipace.adminService.dto.request;

import java.time.OffsetDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "activity_log", schema = "master")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ActivityLogRequest {

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

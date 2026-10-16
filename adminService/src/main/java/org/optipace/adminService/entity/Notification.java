package org.optipace.adminService.entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "notification", schema = "packages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;
    
    @Column(name = "organization_id", nullable = false)
    private Long organizationId;
    
    @Column(name = "recipient_id")
    private Long recipientId;
    
    @Column(name = "title", length = 200, nullable = false)
    private String title;
    
    @Column(name = "message", columnDefinition = "text", nullable = false)
    private String message;
    
    @Column(name = "notification_type", length = 50, nullable = false)
    private String notificationType;
    
    @Column(name = "reference_type", length = 50)
    private String referenceType;
    
    @Column(name = "reference_id")
    private Long referenceId;
    
    @Column(name = "is_read")
    private Boolean isRead = false;
    
    @CreationTimestamp
    @Column(name = "created_on", nullable = false, updatable = false)
    private OffsetDateTime createdOn;
}
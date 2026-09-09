package org.optipace.authService.entity;
// Generated 26-Aug-2026, 8:57:41 pm by Hibernate Tools 6.4.4.Final


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "role_permission"
        , schema = "security"
        , uniqueConstraints = @UniqueConstraint(columnNames = {"role_id", "permission_id"})
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RolePermission {

    @Id
    @Column(name = "role_permission_id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rolePermissionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id", nullable = false)
    private Permission permission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "created_by")
    private Long createdBy;

    @CreationTimestamp
    @Column(name = "created_on", length = 35)
    private LocalDateTime createdOn;

    public RolePermission(Long rolePermissionId, Permission permission, Role role) {
        this.rolePermissionId = rolePermissionId;
        this.permission = permission;
        this.role = role;
    }
}



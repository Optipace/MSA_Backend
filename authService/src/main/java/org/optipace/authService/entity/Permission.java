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
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "permission"
        , schema = "security"
        , uniqueConstraints = @UniqueConstraint(columnNames = "permission_code")
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Permission {

    @Id
    @Column(name = "permission_id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long permissionId;

    @Column(name = "permission_code", unique = true, nullable = false, length = 50)
    private String permissionCode;

    @Column(name = "permission_name", nullable = false, length = 150)
    private String permissionName;

    @Column(name = "module_name", length = 100)
    private String moduleName;

    @Column(name = "description")
    private String description;

    @Column(name = "created_by")
    private Long createdBy;

    @CreationTimestamp
    @Column(name = "created_on", length = 35)
    private LocalDateTime createdOn;

    @Column(name = "version_no")
    private Integer versionNo;

    @Column(name = "record_status", length = 1)
    private Character recordStatus;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "permission")
    private Set<RolePermission> rolePermissions = new HashSet<RolePermission>(0);

    public Permission(Long permissionId, String permissionCode, String permissionName) {
        this.permissionId = permissionId;
        this.permissionCode = permissionCode;
        this.permissionName = permissionName;
    }
}



package org.optipace.authService.entity;
// Generated 26-Aug-2026, 8:57:41 pm by Hibernate Tools 6.4.4.Final


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "role"
        , schema = "security"
        , uniqueConstraints = @UniqueConstraint(columnNames = "role_code")
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Role {

    @Id
    @Column(name = "role_id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

    @Column(name = "role_code", unique = true, nullable = false, length = 30)
    private String roleCode;

    @Column(name = "role_name", nullable = false, length = 100)
    private String roleName;

    @Column(name = "description")
    private String description;

    @Column(name = "is_system_role")
    private Boolean isSystemRole;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "created_by")
    private Long createdBy;

    @CreationTimestamp
    @Column(name = "created_on", length = 35)
    private LocalDateTime createdOn;

    @Column(name = "updated_by")
    private Long updatedBy;

    @UpdateTimestamp
    @Column(name = "updated_on", length = 35)
    private LocalDateTime updatedOn;

    @Column(name = "version_no")
    private Integer versionNo;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "record_status", length = 1)
    private Character recordStatus;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "role")
    private Set<EmployeeRole> employeeRoles = new HashSet<EmployeeRole>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "role")
    private Set<RolePermission> rolePermissions = new HashSet<RolePermission>(0);

    public Role(Long roleId, String roleCode, String roleName) {
        this.roleId = roleId;
        this.roleCode = roleCode;
        this.roleName = roleName;
    }
}



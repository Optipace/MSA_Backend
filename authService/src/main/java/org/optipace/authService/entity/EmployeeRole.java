package org.optipace.authService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "employee_role"
        , schema = "security"
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmployeeRole {

    @Id
    @Column(name = "employee_role_id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeRoleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(name = "effective_from", nullable = false, length = 13)
    private LocalDate effectiveFrom;

    @Column(name = "effective_to", length = 13)
    private LocalDate effectiveTo;

    @Column(name = "is_primary_role")
    private Boolean isPrimaryRole;

    @Column(name = "created_by")
    private Long createdBy;

    @CreationTimestamp
    @Column(name = "created_on", length = 35)
    private LocalDateTime createdOn;

    public EmployeeRole(Long employeeRoleId, Role role, Long employeeId, LocalDate effectiveFrom) {
        this.employeeRoleId = employeeRoleId;
        this.role = role;
        this.employeeId = employeeId;
        this.effectiveFrom = effectiveFrom;
    }
}



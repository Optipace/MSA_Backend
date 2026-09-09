package org.optipace.authService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "employee_login"
        , schema = "security"
        , uniqueConstraints = {
        @UniqueConstraint(columnNames = "employee_id"),
        @UniqueConstraint(columnNames = "username")
}
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmployeeLogin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_login_id", unique = true, nullable = false)
    private Long employeeLoginId;

    @Column(name = "employee_id", unique = true, nullable = false)
    private Long employeeId;

    @Column(name = "username", unique = true, nullable = false, length = 100)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "failed_attempts")
    private Integer failedAttempts;

    @Column(name = "account_locked")
    private Boolean accountLocked;

    @Column(name = "password_expiry_date", length = 13)
    private LocalDate passwordExpiryDate;

    @Column(name = "last_login", length = 35)
    private LocalDateTime lastLogin;

    @Column(name = "last_password_changed", length = 35)
    private LocalDateTime lastPasswordChanged;

    @CreationTimestamp
    @Column(name = "created_on", length = 35)
    private LocalDateTime createdOn;

    public EmployeeLogin(Long employeeLoginId, Long employeeId, String username, String passwordHash) {
        this.employeeLoginId = employeeLoginId;
        this.employeeId = employeeId;
        this.username = username;
        this.passwordHash = passwordHash;
    }

}



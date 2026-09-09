package org.optipace.authService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "login_history"
        , schema = "security"
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "login_history_id", unique = true, nullable = false)
    private UUID loginHistoryId;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(name = "login_time", nullable = false, length = 35)
    private LocalDateTime loginTime;

    @Column(name = "logout_time", length = 35)
    private LocalDateTime logoutTime;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "login_status", length = 20)
    private String loginStatus;

    @Column(name = "failure_reason")
    private String failureReason;

    public LoginHistory(UUID loginHistoryId, Long employeeId, LocalDateTime loginTime) {
        this.loginHistoryId = loginHistoryId;
        this.employeeId = employeeId;
        this.loginTime = loginTime;
    }
}



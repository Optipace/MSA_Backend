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
@Table(name = "password_history"
        , schema = "security"
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PasswordHistory {

    @Id
    @Column(name = "password_history_id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long passwordHistoryId;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "changed_on", length = 35)
    private LocalDateTime changedOn;

    public PasswordHistory(Long passwordHistoryId, Long employeeId, String passwordHash) {
        this.passwordHistoryId = passwordHistoryId;
        this.employeeId = employeeId;
        this.passwordHash = passwordHash;
    }
}



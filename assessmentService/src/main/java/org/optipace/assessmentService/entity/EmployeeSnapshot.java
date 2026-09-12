package org.optipace.assessmentService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "employee_snapshot"
        , schema = "assessment"
        , uniqueConstraints = @UniqueConstraint(columnNames = "assessment_session_id")
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmployeeSnapshot {

    @Id
    @Column(name = "employee_snapshot_id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID employeeSnapshotId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_session_id", unique = true, nullable = false)
    private AssessmentSession assessmentSession;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(name = "employee_code", length = 30)
    private String employeeCode;

    @Column(name = "employee_name", length = 300)
    private String employeeName;

    @Column(name = "organization_name", length = 300)
    private String organizationName;

    @Column(name = "factory_name", length = 300)
    private String factoryName;

    @Column(name = "department_name", length = 300)
    private String departmentName;

    @Column(name = "section_name", length = 300)
    private String sectionName;

    @Column(name = "designation_name", length = 300)
    private String designationName;

    @Column(name = "shift_name", length = 100)
    private String shiftName;

    @Column(name = "experience_years", precision = 5, scale = 2)
    private BigDecimal experienceYears;

    @Column(name = "captured_on", length = 35)
    private LocalDateTime capturedOn;

}



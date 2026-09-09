package org.optipace.masterService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "employee"
        , schema = "master"
        , uniqueConstraints = {@UniqueConstraint(columnNames = "employee_code"), @UniqueConstraint(columnNames = "email")}
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Employee {

    @Id
    @Column(name = "employee_id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporting_manager_id")
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factory_id", nullable = false)
    private Factory factory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id", nullable = false)
    private Shift shift;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "designation_id", nullable = false)
    private Designation designation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    @Column(name = "employee_code", unique = true, nullable = false, length = 30)
    private String employeeCode;

    @Column(name = "biometric_id", length = 50)
    private String biometricId;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "middle_name", length = 100)
    private String middleName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "gender", length = 1)
    private Character gender;

    @Column(name = "date_of_birth", length = 13)
    private Date dateOfBirth;

    @Column(name = "date_of_joining", nullable = false, length = 13)
    private Date dateOfJoining;

    @Column(name = "mobile_number", length = 20)
    private String mobileNumber;

    @Column(name = "email", unique = true, length = 150)
    private String email;

    @Column(name = "aadhaar_number", length = 20)
    private String aadhaarNumber;

    @Column(name = "employment_type", length = 20)
    private String employmentType;

    @Column(name = "experience_years", precision = 5, scale = 2)
    private BigDecimal experienceYears;

    @Column(name = "profile_photo_document_id")
    private UUID profilePhotoDocumentId;

    @Column(name = "created_by")
    private Long createdBy;

    @CreationTimestamp
    @Column(name = "created_on", nullable = false, length = 35)
    private LocalDateTime createdOn;

    @Column(name = "updated_by")
    private Long updatedBy;

    @UpdateTimestamp
    @Column(name = "updated_on", length = 35)
    private LocalDateTime updatedOn;

    @Column(name = "version_no", nullable = false)
    @Version
    private int versionNo;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "record_status", nullable = false, length = 1)
    private char recordStatus;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "employee")
    private Set<Employee> employees = new HashSet<Employee>(0);

    public Employee(Long employeeId, Department department, Factory factory, Shift shift, Organization organization, Designation designation, Section section, String employeeCode, String firstName, Date dateOfJoining, LocalDateTime createdOn, int versionNo, char recordStatus) {
        this.employeeId = employeeId;
        this.department = department;
        this.factory = factory;
        this.shift = shift;
        this.organization = organization;
        this.designation = designation;
        this.section = section;
        this.employeeCode = employeeCode;
        this.firstName = firstName;
        this.dateOfJoining = dateOfJoining;
        this.createdOn = createdOn;
        this.versionNo = versionNo;
        this.recordStatus = recordStatus;
    }
}



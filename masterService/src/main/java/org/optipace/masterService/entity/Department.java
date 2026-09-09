package org.optipace.masterService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "department"
        , schema = "master"
        , uniqueConstraints = {@UniqueConstraint(columnNames = "department_code"), @UniqueConstraint(columnNames = "department_name")}
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Department {

    @Id
    @Column(name = "department_id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long departmentId;

    @Column(name = "department_code", unique = true, nullable = false, length = 20)
    private String departmentCode;

    @Column(name = "department_name", unique = true, nullable = false, length = 100)
    private String departmentName;

    @Column(name = "description")
    private String description;

    @Column(name = "display_order")
    private Integer displayOrder;

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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "department")
    private Set<Employee> employees = new HashSet<Employee>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "department")
    private Set<Section> sections = new HashSet<Section>(0);


    public Department(Long departmentId, String departmentCode, String departmentName, LocalDateTime createdOn, int versionNo, char recordStatus) {
        this.departmentId = departmentId;
        this.departmentCode = departmentCode;
        this.departmentName = departmentName;
        this.createdOn = createdOn;
        this.versionNo = versionNo;
        this.recordStatus = recordStatus;
    }
}



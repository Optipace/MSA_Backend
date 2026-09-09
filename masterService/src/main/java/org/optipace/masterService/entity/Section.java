package org.optipace.masterService.entity;

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
@Table(name = "section"
        , schema = "master"
        , uniqueConstraints = @UniqueConstraint(columnNames = {"factory_id", "department_id", "section_code"})
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Section {

    @Id
    @Column(name = "section_id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sectionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factory_id", nullable = false)
    private Factory factory;

    @Column(name = "section_code", nullable = false, length = 30)
    private String sectionCode;

    @Column(name = "section_name", nullable = false, length = 150)
    private String sectionName;

    @Column(name = "line_number")
    private Integer lineNumber;

    @Column(name = "capacity")
    private Integer capacity;

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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "section")
    private Set<Employee> employees = new HashSet<Employee>(0);

    public Section(Long sectionId, Department department, Factory factory, String sectionCode, String sectionName, LocalDateTime createdOn, int versionNo, char recordStatus) {
        this.sectionId = sectionId;
        this.department = department;
        this.factory = factory;
        this.sectionCode = sectionCode;
        this.sectionName = sectionName;
        this.createdOn = createdOn;
        this.versionNo = versionNo;
        this.recordStatus = recordStatus;
    }
}



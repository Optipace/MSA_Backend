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
@Table(name = "designation"
        , schema = "master"
        , uniqueConstraints = @UniqueConstraint(columnNames = "designation_code")
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Designation {

    @Id
    @Column(name = "designation_id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long designationId;

    @Column(name = "designation_code", unique = true, nullable = false, length = 20)
    private String designationCode;

    @Column(name = "designation_name", nullable = false, length = 150)
    private String designationName;

    @Column(name = "hierarchy_level", nullable = false)
    private int hierarchyLevel;

    @Column(name = "is_manager")
    private Boolean isManager;

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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "designation")
    private Set<Employee> employees = new HashSet<Employee>(0);

    public Designation(Long designationId, String designationCode, String designationName, int hierarchyLevel, LocalDateTime createdOn, int versionNo, char recordStatus) {
        this.designationId = designationId;
        this.designationCode = designationCode;
        this.designationName = designationName;
        this.hierarchyLevel = hierarchyLevel;
        this.createdOn = createdOn;
        this.versionNo = versionNo;
        this.recordStatus = recordStatus;
    }
}



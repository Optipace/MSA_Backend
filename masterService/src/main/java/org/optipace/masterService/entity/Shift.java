package org.optipace.masterService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "shift"
        , schema = "master"
        , uniqueConstraints = @UniqueConstraint(columnNames = {"factory_id", "shift_code"})
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Shift { 

    @Id
    @Column(name = "shift_id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shiftId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factory_id", nullable = false)
    private Factory factory;

    @Column(name = "shift_code", nullable = false, length = 20)
    private String shiftCode;

    @Column(name = "shift_name", nullable = false, length = 100)
    private String shiftName;

    @Column(name = "start_time", nullable = false, length = 15)
    private Time startTime;

    @Column(name = "end_time", nullable = false, length = 15)
    private Time endTime;

    @Column(name = "break_duration_minutes")
    private Integer breakDurationMinutes;

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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "shift")
    private Set<Employee> employees = new HashSet<Employee>(0);

    public Shift(Long shiftId, Factory factory, String shiftCode, String shiftName, Time startTime, Time endTime, LocalDateTime createdOn, int versionNo, char recordStatus) {
        this.shiftId = shiftId;
        this.factory = factory;
        this.shiftCode = shiftCode;
        this.shiftName = shiftName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createdOn = createdOn;
        this.versionNo = versionNo;
        this.recordStatus = recordStatus;
    }
}



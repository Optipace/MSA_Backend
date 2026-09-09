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
@Table(name = "state"
        , schema = "master"
        , uniqueConstraints = @UniqueConstraint(columnNames = {"country_id", "state_code"})
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class State {

    @Id
    @Column(name = "state_id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @Column(name = "state_code", nullable = false, length = 10)
    private String stateCode;

    @Column(name = "state_name", nullable = false, length = 100)
    private String stateName;

    @Column(name = "gst_state_code", length = 5)
    private String gstStateCode;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "created_by")
    private Long createdBy;

    @CreationTimestamp
    @Column(name = "created_on", length = 35)
    private LocalDateTime createdOn;

    @Column(name = "updated_by")
    private Long updatedBy;

    @UpdateTimestamp
    @Column(name = "updated_on", length = 35)
    private LocalDateTime updatedOn;

    @Column(name = "version_no")
    @Version
    private Integer versionNo;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "record_status", length = 1)
    private Character recordStatus;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "state")
    private Set<Organization> organizations = new HashSet<Organization>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "state")
    private Set<City> cities = new HashSet<City>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "state")
    private Set<Factory> factories = new HashSet<Factory>(0);

    public State(Long stateId, Country country, String stateCode, String stateName) {
        this.stateId = stateId;
        this.country = country;
        this.stateCode = stateCode;
        this.stateName = stateName;
    }
}



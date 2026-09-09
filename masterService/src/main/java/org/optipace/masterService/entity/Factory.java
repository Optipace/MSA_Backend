package org.optipace.masterService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "factory"
        , schema = "master"
        , uniqueConstraints = @UniqueConstraint(columnNames = {"organization_id", "factory_code"})
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Factory {

    @Id
    @Column(name = "factory_id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long factoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_id", nullable = false)
    private State state;

    @Column(name = "factory_code", nullable = false, length = 20)
    private String factoryCode;

    @Column(name = "factory_name", nullable = false, length = 200)
    private String factoryName;

    @Column(name = "short_name", length = 50)
    private String shortName;

    @Column(name = "address_line1", length = 300)
    private String addressLine1;

    @Column(name = "address_line2", length = 300)
    private String addressLine2;

    @Column(name = "postal_code", length = 10)
    private String postalCode;

    @Column(name = "latitude", precision = 10, scale = 6)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 10, scale = 6)
    private BigDecimal longitude;

    @Column(name = "contact_person", length = 150)
    private String contactPerson;

    @Column(name = "contact_number", length = 30)
    private String contactNumber;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "capacity_per_day")
    private Integer capacityPerDay;

    @Column(name = "established_on", length = 13)
    private LocalDate establishedOn;

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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "factory")
    private Set<Employee> employees = new HashSet<Employee>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "factory")
    private Set<Section> sections = new HashSet<Section>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "factory")
    private Set<Shift> shifts = new HashSet<Shift>(0);

    public Factory(Long factoryId, City city, Country country, Organization organization, State state, String factoryCode, String factoryName) {
        this.factoryId = factoryId;
        this.city = city;
        this.country = country;
        this.organization = organization;
        this.state = state;
        this.factoryCode = factoryCode;
        this.factoryName = factoryName;
    }
}



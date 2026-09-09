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
@Table(name = "country", schema = "master", uniqueConstraints = {@UniqueConstraint(columnNames = "country_code"), @UniqueConstraint(columnNames = "country_name")})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Country {

    @Id
    @Column(name = "country_id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long countryId;

    @Column(name = "country_code", unique = true, nullable = false, length = 5)
    private String countryCode;

    @Column(name = "country_name", unique = true, nullable = false, length = 100)
    private String countryName;

    @Column(name = "iso_code", length = 5)
    private String isoCode;

    @Column(name = "phone_code", length = 10)
    private String phoneCode;

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

    @Column(name = "version_no")
    @Version
    private Integer versionNo;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "record_status", length = 1)
    private Character recordStatus;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "country")
    private Set<Factory> factories = new HashSet<Factory>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "country")
    private Set<Organization> organizations = new HashSet<Organization>(0);


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "country")
    private Set<State> states = new HashSet<State>(0);

    public Country(Long countryId, String countryCode, String countryName, LocalDateTime createdOn) {
        this.countryId = countryId;
        this.countryCode = countryCode;
        this.countryName = countryName;
        this.createdOn = createdOn;
    }
}



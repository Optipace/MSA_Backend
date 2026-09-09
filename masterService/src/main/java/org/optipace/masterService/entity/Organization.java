package org.optipace.masterService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "organization"
        , schema = "master"
        , uniqueConstraints = {@UniqueConstraint(columnNames = "organization_code"), @UniqueConstraint(columnNames = "gstin")}
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Organization {

    @Id
    @Column(name = "organization_id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long organizationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_id", nullable = false)
    private State state;

    @Column(name = "organization_code", unique = true, nullable = false, length = 20)
    private String organizationCode;

    @Column(name = "organization_name", nullable = false, length = 200)
    private String organizationName;

    @Column(name = "legal_name", nullable = false, length = 300)
    private String legalName;

    @Column(name = "gstin", unique = true, length = 20)
    private String gstin;

    @Column(name = "pan_number", length = 20)
    private String panNumber;

    @Column(name = "registration_number", length = 100)
    private String registrationNumber;

    @Column(name = "address_line1", length = 300)
    private String addressLine1;

    @Column(name = "address_line2", length = 300)
    private String addressLine2;

    @Column(name = "postal_code", length = 10)
    private String postalCode;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "phone_number", length = 30)
    private String phoneNumber;

    @Column(name = "website", length = 200)
    private String website;

    @Column(name = "logo_document_id")
    private UUID logoDocumentId;

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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "organization")
    private Set<Employee> employees = new HashSet<Employee>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "organization")
    private Set<Factory> factories = new HashSet<Factory>(0);

    public Organization(Long organizationId, City city, Country country, State state, String organizationCode, String organizationName, String legalName, LocalDateTime createdOn, int versionNo, char recordStatus) {
        this.organizationId = organizationId;
        this.city = city;
        this.country = country;
        this.state = state;
        this.organizationCode = organizationCode;
        this.organizationName = organizationName;
        this.legalName = legalName;
        this.createdOn = createdOn;
        this.versionNo = versionNo;
        this.recordStatus = recordStatus;
    }
}



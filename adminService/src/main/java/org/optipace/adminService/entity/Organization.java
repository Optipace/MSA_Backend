package org.optipace.adminService.entity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "organization", schema = "master")
@Data
public class Organization {
    
    @Id
    @Column(name = "organization_id")
    private Long organizationId;
    
    @Column(name = "organization_name")
    private String organizationName;
    
    @Column(name = "organization_code")
    private String organizationCode;
}
package org.optipace.adminService.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "employee", schema = "master")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Long employeeId;

    @Column(name = "employee_code")
    private String employeeCode;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "record_status", nullable = false, length = 1)
    private Character recordStatus;

    @Column(name = "created_by")
    private Long createdBy;
    
    @Column(name = "organization_id")
    private Long organizationId;
    
    @Column(name = "updated_by")
    private Long updatedBy;      // Must be Long, not int or String
}
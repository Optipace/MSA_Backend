package com.msa.msa.assessment.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;

@Entity
@Table(name = "employee", schema = "master")
@Getter
@Setter
public class Employee {

    @Id
    @Column(name = "employee_id")
    private Long employeeId;

    @Column(name = "employee_code", nullable = false)
    private String employeeCode;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "last_name")
    private String lastName;

    @JdbcTypeCode(Types.CHAR)
    @Column(name = "gender")
    private String gender;

    @Column(name = "record_status", nullable = false)
    @JdbcTypeCode(Types.CHAR)
    private String recordStatus;
}
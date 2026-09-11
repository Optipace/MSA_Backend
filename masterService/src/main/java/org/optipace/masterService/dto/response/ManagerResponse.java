package org.optipace.masterService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ManagerResponse {
    private Long employeeId;

    private String employeeCode;

    private String firstName;

    private String middleName;

    private String lastName;

    private String biometricId;

    private String gender;

    private Date dateOfBirth;

    private Date dateOfJoining;

    private String mobileNumber;

    private String email;

    private String aadhaarNumber;

    private String employmentType;

    private BigDecimal experienceYears;

    private Long departmentId;

    private Long factoryId;

    private Long shiftId;

    private Long organizationId;

    private Long designationId;

    private Long sectionId;

    private Long roleId;
}

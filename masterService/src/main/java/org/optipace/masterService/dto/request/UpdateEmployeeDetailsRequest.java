package org.optipace.masterService.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEmployeeDetailsRequest {

    @Size(max = 100, message = "First name must not exceed 100 characters")
    private String firstName;

    @Size(max = 100, message = "Middle name must not exceed 100 characters")
    private String middleName;

    @Size(max = 100, message = "Last name must not exceed 100 characters")
    private String lastName;

    @Size(max = 50, message = "Biometric ID must not exceed 50 characters")
    private String biometricId;

    @Pattern(regexp = "^[MFU]$", message = "Gender must be 'M' or 'F' or 'U'")
    private String gender;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateOfBirth;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateOfJoining;

    @Pattern(regexp = "^\\d{10}$", message = "Mobile number must be exactly 10 digits")
    private String mobileNumber;

    @Email(message = "Invalid email format")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    private String email;

    @Pattern(regexp = "^\\d{12}$", message = "Aadhaar number must be exactly 12 digits")
    private String aadhaarNumber;

    private String employmentType;

    private BigDecimal experienceYears;

    private Long departmentId;
    private Long factoryId;
    private Long shiftId;
    private Long organizationId;
    private Long designationId;
    private Long sectionId;
    private Long reportingManagerId;

    private String remarks;
}

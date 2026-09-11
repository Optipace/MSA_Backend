package org.optipace.masterService.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;

@Getter
@Setter
public class AddEmployeeRequest {

    @NotBlank(message = "Employee code is required")
    @Size(max = 30, message = "Code cannot exceed 30 characters")
    private String employeeCode;

    @NotBlank(message = "First name is required")
    @Size(max = 100)
    private String firstName;

    private String middleName;

    private String lastName;

//    @NotBlank(message = "Biometric is required")
    private String biometricId;

    @Pattern(regexp = "^[MFU]$", message = "Gender must be M, F, or U")
    private String gender;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @NotNull(message = "Date of joining is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfJoining;

    private String mobileNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Size(max = 20)
    private String aadhaarNumber;

    private String employmentType;

    private BigDecimal experienceYears;

    @NotNull(message = "Department ID is required")
    private Long departmentId;

    @NotNull(message = "Factory ID is required")
    private Long factoryId;

    @NotNull(message = "Shift ID is required")
    private Long shiftId;

    @NotNull(message = "Organization ID is required")
    private Long organizationId;

    @NotNull(message = "Designation ID is required")
    private Long designationId;

    @NotNull(message = "Section ID is required")
    private Long sectionId;

    private Long reportingManagerId;

    private String remarks;

    @NotNull(message = "Role Id is required")
    private Long roleId;
}
package org.optipace.masterService.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddSingleEmployeeRequest {
    
    // ============ REQUIRED ============
    
    @NotBlank(message = "Employee code is required")
    @Size(max = 30, message = "Employee code must be ≤ 30 characters")
    private String employeeCode;
    
    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must be ≤ 100 characters")
    private String firstName;
    
    @Size(max = 100, message = "Middle name must be ≤ 100 characters")
    private String middleName;
    
    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must be ≤ 100 characters")
    private String lastName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 150, message = "Email must be ≤ 150 characters")
    private String email;
    
    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Mobile number must be 10-15 digits")
    private String mobileNumber;
    
    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;
    
    @NotNull(message = "Date of joining is required")
    @PastOrPresent(message = "Date of joining cannot be in the future")
    private LocalDate dateOfJoining;
    
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
    
    // ============ OPTIONAL ============
    
    @Size(max = 50, message = "Biometric ID must be ≤ 50 characters")
    private String biometricId;
    
    @Pattern(regexp = "^[MFO]$", message = "Gender must be M, F, or O")
    private String gender;
    
    @Pattern(regexp = "^[0-9]{12}$", message = "Aadhaar number must be 12 digits")
    private String aadhaarNumber;
    
    @Size(max = 20, message = "Employment type must be ≤ 20 characters")
    private String employmentType;
    
    @PositiveOrZero(message = "Experience years must be zero or positive")
    private BigDecimal experienceYears;
    
    private Long reportingManagerId;
    
    private String remarks;
    
    private Long roleId;         // for auth service registration
}
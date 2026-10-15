package org.optipace.masterService.dto.request;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddAdminRequest {
    
    @NotBlank(message = "Admin name is required")
    @Size(max = 200, message = "Admin name must be ≤ 200 characters")
    private String adminName;
    
    @NotBlank(message = "Admin ID is required")
    @Size(max = 30, message = "Admin ID must be ≤ 30 characters")
    private String adminId;                 // maps to employeeCode
    
    @NotNull(message = "Organization ID is required")
    private Long organizationId;
    
    @Size(max = 200, message = "Location must be ≤ 200 characters")
    private String location;
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$",
             message = "Provide a valid phone number (10-15 digits)")
    private String phoneNumber;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 150, message = "Email must be ≤ 150 characters")
    private String email;
}
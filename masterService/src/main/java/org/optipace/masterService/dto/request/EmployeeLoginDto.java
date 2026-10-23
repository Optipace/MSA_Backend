package org.optipace.masterService.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
public class EmployeeLoginDto {
    @NotNull
    private Long employeeId;
    
    @NotBlank(message = "Username is mandatory")
    private String username;
    
    @NotBlank(message = "Password is mandatory")
    private String plainTextPassword; // ⭐ Check this name!
    
    @NotNull
    private Long roleId;
}
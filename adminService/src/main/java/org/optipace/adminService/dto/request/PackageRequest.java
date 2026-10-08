package org.optipace.adminService.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PackageRequest {
    
    @NotBlank(message = "Package name is required")
    @Size(max = 100, message = "Package name must be ≤ 100 characters")
    private String packageName;
    
    private String usageDescription;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = true,
            message = "Price must be ≥ 0")
    @Digits(integer = 10, fraction = 2,
            message = "Price must have ≤ 10 digits and ≤ 2 decimals")
    private BigDecimal price;
    
    @Pattern(regexp = "INR|USD", message = "Currency must be INR or USD")
    private String currency = "INR";
    
    @NotNull(message = "Number of days is required")
    @Min(value = 1, message = "Number of days must be > 0")
    private Integer numberOfDays;
    
    private Long usage;
    
    private String recordStatus;   // "A" or "I" — optional (defaults to "A")
}
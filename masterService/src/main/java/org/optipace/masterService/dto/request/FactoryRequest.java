package org.optipace.masterService.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class FactoryRequest {

    @NotBlank(message = "Provide factory code")
    @Size(max = 20, message = "Code cannot exceed more than 20 characters")
    private String factoryCode;

    @NotBlank(message = "Provide factory name")
    @Size(max = 200, message = "Name cannot exceed more than 200 characters")
    private String factoryName;

    @NotBlank(message = "Provide short name for factory")
    @Size(max = 50, message = "Name cannot exceed more than 50 characters")
    private String shortName;

    @NotNull(message = "Provide city the factory belongs to")
    private Long cityId;

    @NotNull(message = "Provide country the factory belongs to")
    private Long countryId;

    @NotNull(message = "Provide state the factory belongs to")
    private Long stateId;

    @NotBlank(message = "Provide the address")
    @Size(max = 300, message = "Address line 1 cannot exceed more than 300 characters")
    private String addressLine1;

    @Size(max = 300, message = "Address line 2 cannot exceed more than 300 characters")
    private String addressLine2;

    @NotBlank(message = "Provide the postal code")
    @Size(max = 10, message = "Postal code cannot exceed more than 10 characters")
    private String postalCode;

    @NotNull(message = "Provide latitude")
    @Digits(integer = 4, fraction = 6, message = "Latitude must have up to 4 integer digits and exactly 6 decimal places")
    private BigDecimal latitude;

    @NotNull(message = "Provide longitude")
    @Digits(integer = 4, fraction = 6, message = "Longitude must have up to 4 integer digits and exactly 6 decimal places")
    private BigDecimal longitude;

    @NotBlank(message = "Provide contact person")
    @Size(max = 150, message = "Contact Person Name cannot exceed more than 150 characters")
    private String contactPerson;

    @NotBlank(message = "Provide contact number")
    @Size(max = 30, message = "Contact number cannot exceed more than 30 characters")
    private String contactNumber;

    @NotBlank(message = "Provide the email")
    @Email(message = "Please provide a valid email")
    @Size(max = 150, message = "Email cannot exceed more than 150 characters")
    private String email;

    @NotNull(message = "Provide factory capacity per day")
    private Integer capacityPerDay;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate establishedOn;

    private String remarks;

    @NotNull(message = "Provide the organization for which the factory belongs")
    private Long organizationId;
}

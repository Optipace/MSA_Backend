package org.optipace.masterService.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class FactoryUpdateRequest {

    @NotNull(message = "Provide the organization for which the factory belongs")
    private Long organizationId;

    @NotBlank(message = "Provide factory code")
    @Size(max = 20, message = "Code cannot exceed more than 20 characters")
    private String factoryCode;

    private Long cityId;

    private Long stateId;

    private Long countryId;

    @Size(max = 200, message = "Name cannot exceed more than 200 characters")
    private String factoryName;

    @Size(max = 50, message = "Name cannot exceed more than 50 characters")
    private String shortName;

    @Size(max = 300, message = "Address line 1 cannot exceed more than 300 characters")
    private String addressLine1;

    @Size(max = 300, message = "Address line 2 cannot exceed more than 300 characters")
    private String addressLine2;

    @Size(max = 10, message = "Postal code cannot exceed more than 10 characters")
    private String postalCode;

    @Digits(integer = 4, fraction = 6, message = "Latitude must have up to 4 integer digits and exactly 6 decimal places")
    private BigDecimal latitude;

    @Digits(integer = 4, fraction = 6, message = "Longitude must have up to 4 integer digits and exactly 6 decimal places")
    private BigDecimal longitude;

    @Size(max = 150, message = "Contact Person Name cannot exceed more than 150 characters")
    private String contactPerson;

    @Size(max = 30, message = "Contact number cannot exceed more than 30 characters")
    private String contactNumber;

    @Email(message = "Please provide a valid email")
    @Size(max = 150, message = "Email cannot exceed more than 150 characters")
    private String email;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Integer capacityPerDay;

    private LocalDate establishedOn;

    private String remarks;
}

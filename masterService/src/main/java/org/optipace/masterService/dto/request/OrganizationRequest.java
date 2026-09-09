package org.optipace.masterService.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class OrganizationRequest {
    @NotBlank(message = "Provide organization code")
    @Size(max = 20, message = "Code cannot exceed 20 characters")
    private String code;

    @NotBlank(message = "Provide organization name")
    @Size(max = 200, message = "Name cannot exceed 200 characters")
    private String name;

    @NotNull(message = "Provide city the organization belongs to")
    private Long cityId;

    @NotNull(message = "Provide country the organization belongs to")
    private Long countryId;

    @NotNull(message = "Provide state the organization belongs to")
    private Long stateId;

    @NotBlank(message = "Provide the legal name")
    @Size(max = 300, message = "Legal name cannot exceed 300 characters")
    private String legalName;

    @NotBlank(message = "Provide the gstin number")
    private String gstin;

    @NotBlank(message = "Provide the pan number")
    private String panNumber;

    @NotBlank(message = "Provide the registration number")
    private String registrationNumber;

    @NotBlank(message = "Provide the address")
    private String addressLine1;

    private String addressLine2;

    @NotBlank(message = "Provide the postal code")
    private String postalCode;

    @NotBlank(message = "Provide the email")
    @Email(message = "Please provide a valid email")
    private String email;

    @NotBlank(message = "Provide phone number")
    @Size(min = 20, message = "Provide a valid phone number")
    private String phoneNumber;

    private String website;

    private String remarks;
}

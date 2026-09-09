package org.optipace.masterService.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrganizationUpdateRequest {

    @Size(max = 20, message = "Code cannot exceed 20 characters")
    private String code;

    @Size(max = 200, message = "Name cannot exceed 200 characters")
    private String name;

    private Long cityId;
    private Long countryId;
    private Long stateId;

    @Size(max = 300, message = "Legal name cannot exceed 300 characters")
    private String legalName;

    private String gstin;
    private String panNumber;
    private String registrationNumber;
    private String addressLine1;
    private String addressLine2;
    private String postalCode;

    @Email(message = "Please provide a valid email")
    private String email;

    @Size(min = 13, message = "Provide a valid phone number")
    private String phoneNumber;

    private String website;
    private String remarks;
}
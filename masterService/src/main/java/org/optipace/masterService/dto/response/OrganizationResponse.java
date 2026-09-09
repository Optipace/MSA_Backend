package org.optipace.masterService.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class OrganizationResponse {
    private Long organizationId;
    private Long cityId;
    private Long countryId;
    private Long stateId;
    private String organizationCode;
    private String organizationName;
    private String legalName;
    private String gstin;
    private String panNumber;
    private String registrationNumber;
    private String addressLine1;
    private String addressLine2;
    private String postalCode;
    private String email;
    private String phoneNumber;
    private String website;
    private UUID logoDocumentId;
    private Long createdBy;
}

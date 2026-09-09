package org.optipace.masterService.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class ListOfFactoriesResponse {
    private Long factoryId;
    private Long cityId;
    private Long countryId;
    private Long stateId;
    private OrganizationResponse organizationResponse;
    private String factoryCode;
    private String factoryName;
    private String shortName;
    private String addressLine1;
    private String addressLine2;
    private String postalCode;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String contactPerson;
    private String contactNumber;
    private String email;
    private Integer capacityPerDay;
    private LocalDate establishedOn;
    private Long createdBy;
    private LocalDateTime createdOn;
}

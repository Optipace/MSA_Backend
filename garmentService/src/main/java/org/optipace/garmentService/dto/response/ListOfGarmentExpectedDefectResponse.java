package org.optipace.garmentService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListOfGarmentExpectedDefectResponse {

    private UUID garmentExpectedDefectId;

    private UUID garmentInstanceId;

    private Long garmentAreaId;

    private Long defectId;

    private Long severityLevelId;

    private OffsetDateTime createdOn;
}
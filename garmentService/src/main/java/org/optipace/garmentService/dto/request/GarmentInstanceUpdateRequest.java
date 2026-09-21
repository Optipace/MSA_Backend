package org.optipace.garmentService.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GarmentInstanceUpdateRequest {

    private String serialNumber;

    private String qrCode;

    private String barcode;

    private String currentStatus;

    private String currentLocation;

    private LocalDate manufacturedOn;

    private UUID garmentLotId;

    private String remarks;

    private String recordStatus;
}
package org.optipace.garmentService.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GarmentInstanceCreateRequest {

    @NotBlank(message = "Serial number is required")
    private String serialNumber;

    @NotNull(message = "QR Code is required")
    private String qrCode;

    @NotNull(message = "BAR Code is required")
    private String barcode;

    private String currentStatus;

    private String currentLocation;

    private LocalDate manufacturedOn;

    @NotNull(message = "Garment lot ID is required")
    private UUID garmentLotId;

    private String remarks;
}
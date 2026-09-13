package com.msa.msa.garment.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class GarmentValidateRequest {

    private UUID assessmentSessionId;

    private UUID assessmentModuleInstanceId;

    private String barcode;
}
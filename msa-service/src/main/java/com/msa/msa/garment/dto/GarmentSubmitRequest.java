package com.msa.msa.garment.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class GarmentSubmitRequest {

    private UUID assessmentSessionId;

    private UUID assessmentModuleInstanceId;

    private UUID garmentInstanceId;

    private List<GarmentSelection> selections;
}
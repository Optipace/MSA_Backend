package com.msa.msa.garment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class GarmentSubmitResponse {

    private UUID assessmentResponseId;

    private UUID garmentInstanceId;

    private int totalExpectedDefects;

    private int correctlyIdentifiedDefects;

    private int totalExpectedAreas;

    private int correctlyIdentifiedAreas;

    private BigDecimal defectScore;

    private BigDecimal areaScore;

    private BigDecimal combinedScore;

    private String result;

    private String status;

    private String nextModule;
}
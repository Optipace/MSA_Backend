package com.msa.msa.garment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class GarmentValidateResponse {

    private boolean valid;

    private UUID garmentInstanceId;

    private String message;
}
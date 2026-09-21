package com.msa.msa.garment.controller;

import com.msa.msa.garment.dto.GarmentSubmitRequest;
import com.msa.msa.garment.dto.GarmentSubmitResponse;
import com.msa.msa.garment.dto.GarmentValidateRequest;
import com.msa.msa.garment.dto.GarmentValidateResponse;
import com.msa.msa.garment.service.GarmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/garments")
public class GarmentController {

    private final GarmentService garmentService;

    public GarmentController(GarmentService garmentService) {
        this.garmentService = garmentService;
    }

    @PostMapping("/validate")
    public ResponseEntity<GarmentValidateResponse> validateGarment(
            @RequestBody GarmentValidateRequest request) {

        return ResponseEntity.ok(
                garmentService.validateGarment(request));
    }

    @PostMapping("/submit")
    public ResponseEntity<GarmentSubmitResponse> submitGarment(
            @RequestBody GarmentSubmitRequest request) {

        return ResponseEntity.ok(
                garmentService.submitGarment(request));
    }
}
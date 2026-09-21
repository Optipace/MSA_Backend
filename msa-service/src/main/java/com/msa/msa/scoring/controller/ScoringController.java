package com.msa.msa.scoring.controller;

import com.msa.msa.scoring.dto.FinalScoreResponse;
import com.msa.msa.scoring.service.ScoringService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.msa.msa.scoring.dto.FinalScoreRequest;

import java.util.UUID;

@RestController
@RequestMapping("/api/scoring")
public class ScoringController {

    private final ScoringService scoringService;

    public ScoringController(ScoringService scoringService) {
        this.scoringService = scoringService;
    }

    @PostMapping("/calculate")
    public ResponseEntity<FinalScoreResponse> calculateFinalScore(
            @RequestBody FinalScoreRequest request
    ) {

        return ResponseEntity.ok(
                scoringService.calculateFinalScore(
                        request.getAssessmentSessionId()
                )
        );
    }
}

package com.msa.msa.scoring.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class FinalScoreResponse {

    private UUID assessmentSessionId;

    private BigDecimal iqScore;
    private BigDecimal colorScore;
    private BigDecimal areaDefectScore;

    private BigDecimal iqWeightage;
    private BigDecimal colorWeightage;
    private BigDecimal areaDefectWeightage;

    private BigDecimal finalScore;

    private Long competencyRatingId;

    private String overallResult;

    private String status;
}

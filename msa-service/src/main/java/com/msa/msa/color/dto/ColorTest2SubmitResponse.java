package com.msa.msa.color.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ColorTest2SubmitResponse {

    private UUID assessmentModuleInstanceId;
    private String moduleCode;
    private Integer correctAnswers;
    private Integer wrongAnswers;
    private Integer passThreshold;
    private String result;
    private String status;
    private String nextModule;

    public ColorTest2SubmitResponse(
            UUID assessmentModuleInstanceId,
            String moduleCode,
            Integer correctAnswers,
            Integer wrongAnswers,
            Integer passThreshold,
            String result,
            String status,
            String nextModule) {

        this.assessmentModuleInstanceId = assessmentModuleInstanceId;
        this.moduleCode = moduleCode;
        this.correctAnswers = correctAnswers;
        this.wrongAnswers = wrongAnswers;
        this.passThreshold = passThreshold;
        this.result = result;
        this.status = status;
        this.nextModule = nextModule;
    }
}
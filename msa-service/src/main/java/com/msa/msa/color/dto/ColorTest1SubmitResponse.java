package com.msa.msa.color.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ColorTest1SubmitResponse {

    private UUID assessmentModuleInstanceId;
    private String moduleCode;
    private Integer correctAnswers;
    private Integer wrongAnswers;
    private Integer passThreshold;
    private String result;
    private String status;
    private String nextModule;
}
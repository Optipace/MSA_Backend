package com.msa.msa.assessment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentProgressResponse {

    private UUID assessmentSessionId;
    private String assessmentNo;
    private String status;
    private String currentModule;
    private List<ModuleProgressResponse> modules;
}
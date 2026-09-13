package com.msa.msa.assessment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ModuleProgressResponse {

    private UUID assessmentModuleInstanceId;
    private String moduleCode;
    private String status;
}
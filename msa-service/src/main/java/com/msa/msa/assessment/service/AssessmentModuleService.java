package com.msa.msa.assessment.service;

import com.msa.msa.assessment.dto.AssessmentModuleRequest;
import com.msa.msa.assessment.dto.AssessmentModuleResponse;
import com.msa.msa.assessment.dto.PageResponse;
import com.msa.msa.assessment.dto.SingleResponse;
import org.springframework.data.domain.Pageable;

public interface AssessmentModuleService {

    SingleResponse<?> createModule(AssessmentModuleRequest request, String adminId);

    SingleResponse<PageResponse<AssessmentModuleResponse>> getAllModules(Pageable pageable);

    SingleResponse<AssessmentModuleResponse> getModuleById(Long moduleId);

    SingleResponse<?> updateModule(Long moduleId, AssessmentModuleRequest request, String adminId);

    SingleResponse<?> deleteModule(Long moduleId);


}

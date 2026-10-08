package com.msa.msa.assessment.service;

import com.msa.msa.assessment.dto.*;
import org.springframework.data.domain.Pageable;

public interface AssessmentModuleService {

    SingleResponse<?> createModule(AssessmentModuleRequest request);

    SingleResponse<PageResponse<AssessmentModuleResponse>> getAllModules(Pageable pageable);

    SingleResponse<AssessmentModuleResponse> getModuleById(Long moduleId);

    SingleResponse<?> updateModule(Long moduleId, AssessmentModuleUpdateRequest request);

    SingleResponse<?> deleteModule(Long moduleId);


}

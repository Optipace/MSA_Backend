package com.msa.msa.assessment.service.IMPL;

import com.msa.msa.assessment.dto.AssessmentModuleRequest;
import com.msa.msa.assessment.dto.AssessmentModuleResponse;
import com.msa.msa.assessment.dto.PageResponse;
import com.msa.msa.assessment.dto.SingleResponse;
import com.msa.msa.assessment.entity.AssessmentModule;
import com.msa.msa.assessment.enums.CustomStatus;
import com.msa.msa.assessment.repository.AssessmentModuleRepository;
import com.msa.msa.assessment.service.AssessmentModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssessmentModuleServiceImpl implements AssessmentModuleService {

    private final AssessmentModuleRepository assessmentModuleRepository;

    @Override
    public SingleResponse<?> createModule(AssessmentModuleRequest request, String adminId) {
        AssessmentModule module = new AssessmentModule();
        mapRequestToEntity(request, module);

        module.setCreatedOn(OffsetDateTime.now());
        module.setRecordStatus('A'); // Assuming 'A' stands for Active

        assessmentModuleRepository.save(module);
        return new SingleResponse<>("Assessment Module created successfully", CustomStatus.SUCCESS);
    }

    @Override
    public SingleResponse<PageResponse<AssessmentModuleResponse>> getAllModules(Pageable pageable) {
        Page<AssessmentModule> modulePage = assessmentModuleRepository.findAll(pageable);

        List<AssessmentModuleResponse> content = modulePage.getContent().stream().map(this::mapEntityToResponse).collect(Collectors.toList());

        PageResponse<AssessmentModuleResponse> pageResponse = new PageResponse<>();
        pageResponse.setContent(content);
        pageResponse.setPageNumber(modulePage.getNumber());
        pageResponse.setPageSize(modulePage.getSize());
        pageResponse.setTotalElements(modulePage.getTotalElements());
        pageResponse.setTotalPages(modulePage.getTotalPages());
        pageResponse.setLast(modulePage.isLast());

        return new SingleResponse<>(pageResponse, CustomStatus.SUCCESS);
    }

    @Override
    public SingleResponse<AssessmentModuleResponse> getModuleById(Long moduleId) {
        AssessmentModule module = assessmentModuleRepository.findById(moduleId).orElseThrow(() -> new RuntimeException("Assessment Module not found with id: " + moduleId));

        return new SingleResponse<>(mapEntityToResponse(module), CustomStatus.SUCCESS);
    }

    @Override
    public SingleResponse<?> updateModule(Long moduleId, AssessmentModuleRequest request, String adminId) {
        AssessmentModule module = assessmentModuleRepository.findById(moduleId).orElseThrow(() -> new RuntimeException("Assessment Module not found with id: " + moduleId));

        mapRequestToEntity(request, module);
        module.setUpdatedOn(OffsetDateTime.now());

        assessmentModuleRepository.save(module);
        return new SingleResponse<>("Assessment Module updated successfully", CustomStatus.SUCCESS);
    }

    @Override
    public SingleResponse<?> deleteModule(Long moduleId) {

        Optional<AssessmentModule> response = assessmentModuleRepository.findById(moduleId);

        if (response.isEmpty()) {
            return new SingleResponse<>("Assessment Module not found", CustomStatus.FAILURE);
        }

        AssessmentModule module = response.get();
        if (module.getRecordStatus() != 'A') {
            return new SingleResponse<>("Assessment Module is already inactive", CustomStatus.FAILURE);
        }

        module.setRecordStatus('I');
        module.setUpdatedOn(OffsetDateTime.now());
        assessmentModuleRepository.save(module);
        return new SingleResponse<>("Assessment Module deleted successfully", CustomStatus.SUCCESS);
    }


    private void mapRequestToEntity(AssessmentModuleRequest request, AssessmentModule module) {
        module.setModuleCode(request.getModuleCode());
        module.setModuleName(request.getModuleName());
        module.setDescription(request.getDescription());
        module.setDisplayOrder(request.getDisplayOrder());
        module.setDurationMinutes(request.getDurationMinutes());
        module.setIsScored(request.getIsScored());
        module.setIsMandatory(request.getIsMandatory());
    }

    private AssessmentModuleResponse mapEntityToResponse(AssessmentModule module) {
        AssessmentModuleResponse response = new AssessmentModuleResponse();
        response.setAssessmentModuleId(module.getAssessmentModuleId());
        response.setModuleCode(module.getModuleCode());
        response.setModuleName(module.getModuleName());
        response.setDescription(module.getDescription());
        response.setDisplayOrder(module.getDisplayOrder());
        response.setDurationMinutes(module.getDurationMinutes());
        response.setIsScored(module.getIsScored());
        response.setIsMandatory(module.getIsMandatory());
        response.setCreatedOn(module.getCreatedOn());
        response.setUpdatedOn(module.getUpdatedOn());
        response.setRecordStatus(module.getRecordStatus());
        return response;
    }
}

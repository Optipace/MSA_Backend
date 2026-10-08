package com.msa.msa.assessment.service.IMPL;

import com.msa.msa.assessment.dto.*;
import com.msa.msa.assessment.entity.AssessmentModule;
import com.msa.msa.assessment.enums.CustomStatus;
import com.msa.msa.assessment.repository.AssessmentModuleRepository;
import com.msa.msa.assessment.service.AssessmentModuleService;
import com.msa.msa.common.exception.ResourceNotFoundException;
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
    public SingleResponse<?> createModule(AssessmentModuleRequest request) {

        if (assessmentModuleRepository.existsByModuleCodeAndRecordStatus(request.getModuleCode(), 'A')) {
            return new SingleResponse<>("Module code already exists: " + request.getModuleCode(), CustomStatus.FAILURE);
        }

        AssessmentModule module = new AssessmentModule();
        mapRequestToEntity(request, module);
        Integer maxDisplayOrder = assessmentModuleRepository.findMaxDisplayOrder();

        module.setDisplayOrder(maxDisplayOrder + 1);
        module.setCreatedOn(OffsetDateTime.now());
        module.setRecordStatus('A');

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

        AssessmentModule module = assessmentModuleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("ModuleId not found with Id: " + moduleId));

        return new SingleResponse<>(mapEntityToResponse(module), CustomStatus.SUCCESS);
    }

    @Override
    public SingleResponse<?> updateModule(Long moduleId, AssessmentModuleUpdateRequest request) {

        AssessmentModule module = assessmentModuleRepository.findById(moduleId).orElseThrow(() -> new ResourceNotFoundException("Assessment Module not found with id: " + moduleId));

        // Update only fields which are provided in request
        if (request.getModuleCode() != null) {
            module.setModuleCode(request.getModuleCode());
        }

        if (request.getModuleName() != null) {
            module.setModuleName(request.getModuleName());
        }

        if (request.getDescription() != null) {
            module.setDescription(request.getDescription());
        }

        if (request.getDurationMinutes() != null) {
            module.setDurationMinutes(request.getDurationMinutes());
        }

        if (request.getIsScored() != null) {
            module.setIsScored(request.getIsScored());
        }

        if (request.getIsMandatory() != null) {
            module.setIsMandatory(request.getIsMandatory());
        }

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

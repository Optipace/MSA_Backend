package org.optipace.masterService.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.optipace.masterService.dto.request.DesignationRequest;
import org.optipace.masterService.dto.request.DesignationUpdateRequest;
import org.optipace.masterService.dto.response.DesignationResponse;
import org.optipace.masterService.dto.response.PageResponse;
import org.optipace.masterService.dto.response.SingleResponse;
import org.optipace.masterService.entity.Designation;
import org.optipace.masterService.enums.CustomStatus;
import org.optipace.masterService.exception.BadRequestException;
import org.optipace.masterService.exception.NotFoundException;
import org.optipace.masterService.repository.DesignationRepository;
import org.optipace.masterService.service.DesignationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DesignationServiceImpl implements DesignationService {
    private final DesignationRepository designationRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public SingleResponse<?> createDesignation(DesignationRequest request, String adminId) {
        if (designationRepository.existsByDesignationCode(request.getDesignationCode())) {
            throw new BadRequestException("Designation code already exists");
        }

        Designation designation = new Designation();
        designation.setDesignationCode(request.getDesignationCode());
        designation.setDesignationName(request.getDesignationName());
//        designation.setHierarchyLevel(request.getHierarchyLevel());
        designation.setIsManager(request.getIsManager() != null ? request.getIsManager() : false);
        designation.setRemarks(request.getRemarks());
        designation.setCreatedBy(Long.parseLong(adminId));
        designation.setRecordStatus('A');

        designationRepository.save(designation);

        return new SingleResponse<>("Designation created successfully", CustomStatus.SUCCESS);
    }

    @Override
    @Transactional
    public SingleResponse<?> updateDesignation(Long id, DesignationUpdateRequest request, String adminId) {
        Designation designation = designationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Designation not found"));

        if (request.getDesignationCode() != null) {
            if (designationRepository.existsByDesignationCodeAndDesignationIdNot(request.getDesignationCode(), id)) {
                throw new BadRequestException("Designation code already in use");
            }
            designation.setDesignationCode(request.getDesignationCode());
        }

        if (request.getDesignationName() != null) designation.setDesignationName(request.getDesignationName());
//        if (request.getHierarchyLevel() != null) designation.setHierarchyLevel(request.getHierarchyLevel());
        if (request.getIsManager() != null) designation.setIsManager(request.getIsManager());
        if (request.getRemarks() != null) designation.setRemarks(request.getRemarks());

        designation.setUpdatedBy(Long.parseLong(adminId));
        designationRepository.save(designation);

        return new SingleResponse<>("Designation updated successfully", CustomStatus.SUCCESS);
    }

    @Override
    public SingleResponse<DesignationResponse> getDesignationById(Long id) {
        Designation designation = designationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Designation not found"));

        DesignationResponse response = modelMapper.map(designation, DesignationResponse.class);
        return new SingleResponse<>(response, CustomStatus.SUCCESS);
    }

    @Override
    public SingleResponse<PageResponse<DesignationResponse>> getAllDesignations(Pageable pageable) {
        Page<Designation> designationPage = designationRepository.findByRecordStatusOrderByHierarchyLevelAsc('A', pageable);

        List<DesignationResponse> content = designationPage.getContent().stream()
                .map(designation -> modelMapper.map(designation, DesignationResponse.class))
                .collect(Collectors.toList());

        PageResponse<DesignationResponse> pageResponse = new PageResponse<>(
                content,
                designationPage.getNumber(),
                designationPage.getSize(),
                designationPage.getTotalElements(),
                designationPage.getTotalPages(),
                designationPage.isLast()
        );

        return new SingleResponse<>(pageResponse, CustomStatus.SUCCESS);
    }

    @Override
    @Transactional
    public SingleResponse<?> deleteDesignation(Long id, String adminId) {
        Designation designation = designationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Designation not found"));

        designation.setRecordStatus('D');
        designation.setUpdatedBy(Long.parseLong(adminId));
        designationRepository.save(designation);

        return new SingleResponse<>("Designation deleted successfully", CustomStatus.SUCCESS);
    }
}

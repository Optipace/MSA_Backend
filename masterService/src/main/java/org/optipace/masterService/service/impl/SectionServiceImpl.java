package org.optipace.masterService.service.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.optipace.masterService.dto.request.SectionRequest;
import org.optipace.masterService.dto.request.SectionUpdateRequest;
import org.optipace.masterService.dto.response.*;
import org.optipace.masterService.entity.*;
import org.optipace.masterService.enums.CustomStatus;
import org.optipace.masterService.exception.BadRequestException;
import org.optipace.masterService.exception.NotFoundException;
import org.optipace.masterService.repository.*;
import org.optipace.masterService.service.SectionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SectionServiceImpl implements SectionService {

    private final SectionRepository sectionRepository;
    private final ModelMapper modelMapper;
    private final DepartmentRepository departmentRepository;
    private final FactoryRepository factoryRepository;


    @Override
    public SingleResponse<PageResponse<ListOfSectionResponse>> getAllSections(Pageable pageable) {
        Pageable sortedPageable = pageable.getSort().isSorted() ? pageable : PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Order.asc("sectionName").nullsLast()));

        Page<Section> activeSectionPage = sectionRepository.findByRecordStatus('A', sortedPageable);
        List<Section> sectionList = activeSectionPage.getContent();

        List<ListOfSectionResponse> sectionResponseList = sectionList.stream()
                .map((section) -> modelMapper.map(section, ListOfSectionResponse.class))
                .toList();

        PageResponse<ListOfSectionResponse> pageResponse = new PageResponse<>(
                sectionResponseList,
                activeSectionPage.getNumber(),
                activeSectionPage.getSize(),
                activeSectionPage.getTotalElements(),
                activeSectionPage.getTotalPages(),
                activeSectionPage.isLast()
        );
        return new SingleResponse<>(pageResponse, CustomStatus.SUCCESS);

    }

    @Override
    @Transactional
    public SingleResponse<?> createSection(SectionRequest request, String adminId) {
        if (sectionRepository.existsBySectionCode(request.getSectionCode())) {
            throw new BadRequestException("Section code already exists");
        }

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new BadRequestException("Department not found with ID: " + request.getDepartmentId()));

        Factory factory = factoryRepository.findById(request.getFactoryId())
                .orElseThrow(() -> new BadRequestException("Factory not found with ID: " + request.getFactoryId()));

        Section section = new Section();
        section.setDepartment(department);
        section.setFactory(factory);
        section.setSectionCode(request.getSectionCode());
        section.setSectionName(request.getSectionName());
        section.setLineNumber(request.getLineNumber());
        section.setCapacity(request.getCapacity());
        section.setCreatedBy(request.getCreatedBy());
        section.setRemarks(request.getRemarks());
        section.setRecordStatus(request.getRecordStatus());
        sectionRepository.save(section);

        return new SingleResponse<>("Section created successfully", CustomStatus.SUCCESS);
    }

    @Override
    @Transactional
    public SingleResponse<?> updateSection(Long sectionId, SectionUpdateRequest request, String adminId) {

        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new NotFoundException("Section not found"));

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new NotFoundException("Department not found"));

        Factory factory = factoryRepository.findById(request.getFactoryId())
                .orElseThrow(() -> new NotFoundException("Factory not found"));

        boolean exists = sectionRepository.existsByFactory_FactoryIdAndDepartment_DepartmentIdAndSectionCodeAndSectionIdNot(
                request.getFactoryId(), request.getDepartmentId(), request.getSectionCode(), sectionId);

        if (exists) {
            throw new BadRequestException("Section code already exists for this factory and department");
        }

        // Update section details
        section.setDepartment(department);
        section.setFactory(factory);
        section.setSectionCode(request.getSectionCode());
        section.setSectionName(request.getSectionName());
        section.setLineNumber(request.getLineNumber());
        section.setCapacity(request.getCapacity());
        section.setRemarks(request.getRemarks());
        section.setRecordStatus(request.getRecordStatus());

        section.setUpdatedBy(Long.parseLong(adminId));
        sectionRepository.save(section);

        return new SingleResponse<>("Section updated successfully", CustomStatus.SUCCESS);
    }

    @Override
    public SingleResponse<SectionResponse> getSectionById(Long sectionId) {
        Section response = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new NotFoundException("Section not found with this id"));

        SectionResponse sectionResponse = modelMapper.map(response, SectionResponse.class);
        return new SingleResponse<>(sectionResponse, CustomStatus.SUCCESS);
    }

    @Override
    public SingleResponse<?> deleteSectionById(Long sectionId, String adminId) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new NotFoundException("Section not found with this id"));

        section.setRecordStatus('D');
        section.setUpdatedBy(Long.parseLong(adminId));
        sectionRepository.save(section);

        return new SingleResponse<>("Section deleted successfully", CustomStatus.SUCCESS);
    }
}

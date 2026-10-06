package org.optipace.garmentService.service.impl;

import java.util.List;

import org.optipace.garmentService.dto.request.AddDefectCategoryRequest;
import org.optipace.garmentService.dto.request.UpdateDefectCategoryRequest;
import org.optipace.garmentService.dto.response.DefectCategoryResponse;
import org.optipace.garmentService.dto.response.ListOfDefectCategoryResponse;
import org.optipace.garmentService.dto.response.PageResponse;
import org.optipace.garmentService.dto.response.SingleResponse;
import org.optipace.garmentService.entity.DefectCategory;
import org.optipace.garmentService.exception.BadRequestException;
import org.optipace.garmentService.exception.NotFoundException;
import org.optipace.garmentService.repository.DefectCategoryRepository;
import org.optipace.garmentService.service.DefectCategoryService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class DefectCategoryServiceImpl implements DefectCategoryService {

    private final DefectCategoryRepository defectCategoryRepository;

    // ---------- CREATE ----------

    @Override
    @Transactional
    public SingleResponse<?> createDefectCategory(AddDefectCategoryRequest request, String adminId) {

        log.info("Initiating defect category creation for code: {} by Admin: {}",
                request.getCategoryCode(), adminId);

        String categoryCode = null;

        if (request.getCategoryCode() != null && !request.getCategoryCode().trim().isEmpty()) {

            categoryCode = request.getCategoryCode().trim().toUpperCase();

            if (defectCategoryRepository.existsByCategoryCode(categoryCode)) {

                throw new BadRequestException(
                        "Defect category with code " + categoryCode + " already exists");
            }
        }

        String categoryName = null;

        if (request.getCategoryName() != null && !request.getCategoryName().trim().isEmpty()) {

            categoryName = request.getCategoryName().trim();
        }

        DefectCategory defectCategory = new DefectCategory();

        defectCategory.setCategoryCode(categoryCode);
        defectCategory.setCategoryName(categoryName);
        defectCategory.setDescription(request.getDescription());
        defectCategory.setDisplayOrder(request.getDisplayOrder());

        DefectCategory saved = defectCategoryRepository.save(defectCategory);

        log.info("Defect category successfully created with ID: {}", saved.getDefectCategoryId());

        return SingleResponse.success("Defect category created successfully"
                + (saved.getCategoryCode() != null ? " with code: " + saved.getCategoryCode() : ""));
    }

    // ---------- GET ALL ----------

    @Override
    @Transactional(readOnly = true)
    public SingleResponse<PageResponse<ListOfDefectCategoryResponse>> getAllDefectCategories(Pageable pageable) {

        Pageable sortedPageable = pageable.getSort().isSorted()
                ? pageable
                : PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        Sort.by(Sort.Order.asc("categoryName").nullsLast()));

        Page<DefectCategory> defectCategoryPage =
                defectCategoryRepository.findAll(sortedPageable);

        PageResponse<ListOfDefectCategoryResponse> pageResponse =
                PageResponse.of(defectCategoryPage.map(defectCategory ->
                        new ListOfDefectCategoryResponse(
                                defectCategory.getDefectCategoryId(),
                                defectCategory.getCategoryCode(),
                                defectCategory.getCategoryName(),
                                defectCategory.getDescription(),
                                defectCategory.getDisplayOrder())));

        return SingleResponse.success(pageResponse);
    }

    // ---------- GET BY ID ----------

    @Override
    @Transactional(readOnly = true)
    public SingleResponse<DefectCategoryResponse> getDefectCategoryById(Long defectCategoryId) {

        DefectCategory defectCategory = defectCategoryRepository
                .findByDefectCategoryId(defectCategoryId)
                .orElseThrow(() -> new NotFoundException(
                        "Defect category not found with ID: " + defectCategoryId));

        DefectCategoryResponse response = mapToResponse(defectCategory);

        return SingleResponse.success(response);
    }

    // ---------- UPDATE ----------

    @Override
    @Transactional
    public SingleResponse<?> updateDefectCategory(Long defectCategoryId,
                                                  UpdateDefectCategoryRequest request,
                                                  String adminId) {

        log.info("Initiating defect category update for ID: {} by Admin: {}",
                defectCategoryId, adminId);

        DefectCategory defectCategory = defectCategoryRepository
                .findByDefectCategoryId(defectCategoryId)
                .orElseThrow(() -> new NotFoundException(
                        "Defect category not found with ID: " + defectCategoryId));

        String categoryCode = null;

        if (request.getCategoryCode() != null && !request.getCategoryCode().trim().isEmpty()) {

            categoryCode = request.getCategoryCode().trim().toUpperCase();

            if (defectCategoryRepository
                    .existsByCategoryCodeAndDefectCategoryIdNot(categoryCode, defectCategoryId)) {

                throw new BadRequestException(
                        "Defect category with code " + categoryCode + " already exists");
            }
        }

        String categoryName = null;

        if (request.getCategoryName() != null && !request.getCategoryName().trim().isEmpty()) {

            categoryName = request.getCategoryName().trim();
        }

        defectCategory.setCategoryCode(categoryCode);
        defectCategory.setCategoryName(categoryName);
        defectCategory.setDescription(request.getDescription());
        defectCategory.setDisplayOrder(request.getDisplayOrder());

        defectCategoryRepository.save(defectCategory);

        log.info("Defect category successfully updated with ID: {}", defectCategoryId);

        return SingleResponse.success("Defect category updated successfully");
    }

    // ---------- DELETE ----------

    @Override
    @Transactional
    public SingleResponse<?> deleteDefectCategoryById(Long defectCategoryId, String adminId) {

        log.info("Initiating defect category deletion for ID: {} by Admin: {}",
                defectCategoryId, adminId);

        DefectCategory defectCategory = defectCategoryRepository
                .findByDefectCategoryId(defectCategoryId)
                .orElseThrow(() -> new NotFoundException(
                        "Defect category not found with ID: " + defectCategoryId));

        defectCategoryRepository.delete(defectCategory);

        log.info("Defect category successfully deleted with ID: {}", defectCategoryId);

        return SingleResponse.success("Defect category deleted successfully");
    }

    // ---------- MAPPER ----------

    private DefectCategoryResponse mapToResponse(DefectCategory defectCategory) {

        return new DefectCategoryResponse(
                defectCategory.getDefectCategoryId(),
                defectCategory.getCategoryCode(),
                defectCategory.getCategoryName(),
                defectCategory.getDescription(),
                defectCategory.getDisplayOrder());
    }
}
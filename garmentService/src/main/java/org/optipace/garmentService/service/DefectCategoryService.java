package org.optipace.garmentService.service;

import org.optipace.garmentService.dto.request.AddDefectCategoryRequest;
import org.optipace.garmentService.dto.request.UpdateDefectCategoryRequest;
import org.optipace.garmentService.dto.response.DefectCategoryResponse;
import org.optipace.garmentService.dto.response.ListOfDefectCategoryResponse;
import org.optipace.garmentService.dto.response.PageResponse;
import org.optipace.garmentService.dto.response.SingleResponse;
import org.springframework.data.domain.Pageable;

public interface DefectCategoryService {

    SingleResponse<?> createDefectCategory(AddDefectCategoryRequest request, String adminId);

    SingleResponse<PageResponse<ListOfDefectCategoryResponse>>
            getAllDefectCategories(Pageable pageable);

    SingleResponse<DefectCategoryResponse> getDefectCategoryById(Long defectCategoryId);

    SingleResponse<?> updateDefectCategory(Long defectCategoryId,
                                           UpdateDefectCategoryRequest request,
                                           String adminId);

    SingleResponse<?> deleteDefectCategoryById(Long defectCategoryId, String adminId);
}
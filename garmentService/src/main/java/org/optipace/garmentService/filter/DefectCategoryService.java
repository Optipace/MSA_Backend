package org.optipace.garmentService.filter;

import org.optipace.garmentService.dto.request.DefectCategoryRequestDTO;
import org.optipace.garmentService.dto.request.DefectCategorySearchDTO;
import org.optipace.garmentService.dto.response.DefectCategoryResponseDTO;
import org.optipace.garmentService.dto.response.PageResponse;
import org.optipace.garmentService.dto.response.SingleResponse;
import org.springframework.data.domain.Pageable;

public interface DefectCategoryService {
    
    // CREATE
    SingleResponse<DefectCategoryResponseDTO> createDefectCategory(DefectCategoryRequestDTO requestDTO);
    
    // UPDATE
    SingleResponse<DefectCategoryResponseDTO> updateDefectCategory(Long id, DefectCategoryRequestDTO requestDTO);
    
    // DELETE
    SingleResponse<Void> deleteDefectCategory(Long id);
    
    // GET BY ID
    SingleResponse<DefectCategoryResponseDTO> getDefectCategoryById(Long id);
    
    // GET BY CODE
    SingleResponse<DefectCategoryResponseDTO> getDefectCategoryByCode(String code);
    
    // GET ALL (paginated)
    SingleResponse<PageResponse<DefectCategoryResponseDTO>> getAllDefectCategories(Pageable pageable);
    
    // SEARCH (dynamic filters + pagination)
    SingleResponse<PageResponse<DefectCategoryResponseDTO>> searchDefectCategories(
            DefectCategorySearchDTO searchDTO, Pageable pageable);
}
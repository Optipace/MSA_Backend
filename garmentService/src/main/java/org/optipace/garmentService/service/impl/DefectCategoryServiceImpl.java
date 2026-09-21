package org.optipace.garmentService.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.optipace.garmentService.dto.request.DefectCategoryRequestDTO;
import org.optipace.garmentService.dto.request.DefectCategorySearchDTO;
import org.optipace.garmentService.dto.response.DefectCategoryResponseDTO;
import org.optipace.garmentService.dto.response.PageResponse;
import org.optipace.garmentService.dto.response.SingleResponse;
import org.optipace.garmentService.entity.DefectCategory;
import org.optipace.garmentService.exception.ResourceAlreadyExistsException;
import org.optipace.garmentService.exception.ResourceNotFoundException;
import org.optipace.garmentService.filter.DefectCategoryService;
import org.optipace.garmentService.repository.DefectCategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefectCategoryServiceImpl implements DefectCategoryService {
    
    private final DefectCategoryRepository defectCategoryRepository;
    private final ModelMapper modelMapper;
    
    @Override
    @Transactional
    public SingleResponse<DefectCategoryResponseDTO> createDefectCategory(DefectCategoryRequestDTO requestDTO) {
        if (defectCategoryRepository.existsByCategoryCode(requestDTO.getCategoryCode())) {
            throw new ResourceAlreadyExistsException("DefectCategory", "categoryCode", requestDTO.getCategoryCode());
        }
        DefectCategory entity = modelMapper.map(requestDTO, DefectCategory.class);
        entity.setCategoryCode(requestDTO.getCategoryCode().toUpperCase());
        if (entity.getDisplayOrder() == null) entity.setDisplayOrder(1);
        
        DefectCategory saved = defectCategoryRepository.save(entity);
        return SingleResponse.success(
            modelMapper.map(saved, DefectCategoryResponseDTO.class),
            "DEFECT CATEGORY CREATED SUCCESSFULLY"
        );
    }
    
    @Override
    @Transactional
    public SingleResponse<DefectCategoryResponseDTO> updateDefectCategory(Long id, DefectCategoryRequestDTO requestDTO) {
        DefectCategory existing = defectCategoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("DefectCategory", "defectCategoryId", id));
        
        if (defectCategoryRepository.existsByCategoryCodeAndDefectCategoryIdNot(requestDTO.getCategoryCode(), id)) {
            throw new ResourceAlreadyExistsException("DefectCategory", "categoryCode", requestDTO.getCategoryCode());
        }
        existing.setCategoryCode(requestDTO.getCategoryCode().toUpperCase());
        existing.setCategoryName(requestDTO.getCategoryName());
        existing.setDescription(requestDTO.getDescription());
        existing.setDisplayOrder(requestDTO.getDisplayOrder() != null ? requestDTO.getDisplayOrder() : 1);
        
        DefectCategory updated = defectCategoryRepository.save(existing);
        return SingleResponse.success(
            modelMapper.map(updated, DefectCategoryResponseDTO.class),
            "DEFECT CATEGORY UPDATED SUCCESSFULLY"
        );
    }
    
    @Override
    @Transactional
    public SingleResponse<Void> deleteDefectCategory(Long id) {
        DefectCategory entity = defectCategoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("DefectCategory", "defectCategoryId", id));
        defectCategoryRepository.delete(entity);
        return SingleResponse.success(null, "DEFECT CATEGORY DELETED SUCCESSFULLY");
    }
    
    @Override
    @Transactional(readOnly = true)
    public SingleResponse<DefectCategoryResponseDTO> getDefectCategoryById(Long id) {
        DefectCategory entity = defectCategoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("DefectCategory", "defectCategoryId", id));
        return SingleResponse.success(modelMapper.map(entity, DefectCategoryResponseDTO.class));
    }
    
    @Override
    @Transactional(readOnly = true)
    public SingleResponse<DefectCategoryResponseDTO> getDefectCategoryByCode(String code) {
        DefectCategory entity = defectCategoryRepository.findByCategoryCode(code)
            .orElseThrow(() -> new ResourceNotFoundException("DefectCategory", "categoryCode", code));
        return SingleResponse.success(modelMapper.map(entity, DefectCategoryResponseDTO.class));
    }
    
    @Override
    @Transactional(readOnly = true)
    public SingleResponse<PageResponse<DefectCategoryResponseDTO>> getAllDefectCategories(Pageable pageable) {
        Page<DefectCategoryResponseDTO> dtoPage = defectCategoryRepository
            .findAll(pageable)
            .map(entity -> modelMapper.map(entity, DefectCategoryResponseDTO.class));
        return SingleResponse.success(PageResponse.of(dtoPage));
    }
    
    @Override
    @Transactional(readOnly = true)
    public SingleResponse<PageResponse<DefectCategoryResponseDTO>> searchDefectCategories(
            DefectCategorySearchDTO searchDTO, Pageable pageable) {
        Page<DefectCategoryResponseDTO> dtoPage = defectCategoryRepository
            .search(searchDTO.getCategoryCode(), searchDTO.getCategoryName(),
                    searchDTO.getDescription(), searchDTO.getDisplayOrder(), pageable)
            .map(entity -> modelMapper.map(entity, DefectCategoryResponseDTO.class));
        return SingleResponse.success(PageResponse.of(dtoPage));
    }
}
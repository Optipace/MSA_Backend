package org.optipace.garmentService.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.optipace.garmentService.dto.request.DefectCategoryRequestDTO;
import org.optipace.garmentService.dto.request.DefectCategorySearchDTO;
import org.optipace.garmentService.dto.response.DefectCategoryResponseDTO;
import org.optipace.garmentService.dto.response.PageResponse;
import org.optipace.garmentService.dto.response.SingleResponse;
import org.optipace.garmentService.filter.DefectCategoryService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/defect-categories")
@RequiredArgsConstructor
public class DefectCategoryController {
    
    private final DefectCategoryService defectCategoryService;
    
    // ============ CREATE ============
    @PostMapping
    public ResponseEntity<SingleResponse<DefectCategoryResponseDTO>> create(
            @Valid @RequestBody DefectCategoryRequestDTO requestDTO) {
        log.info("REST request to create DefectCategory: {}", requestDTO);
        return new ResponseEntity<>(
            defectCategoryService.createDefectCategory(requestDTO),
            HttpStatus.CREATED
        );
    }
    
    // ============ UPDATE ============
    @PutMapping("/{id}")
    public ResponseEntity<SingleResponse<DefectCategoryResponseDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody DefectCategoryRequestDTO requestDTO) {
        log.info("REST request to update DefectCategory ID: {}", id);
        return ResponseEntity.ok(defectCategoryService.updateDefectCategory(id, requestDTO));
    }
    
    // ============ DELETE ============
    @DeleteMapping("/{id}")
    public ResponseEntity<SingleResponse<Void>> delete(@PathVariable Long id) {
        log.info("REST request to delete DefectCategory ID: {}", id);
        return ResponseEntity.ok(defectCategoryService.deleteDefectCategory(id));
    }
    
    // ============ GET BY ID ============
    @GetMapping("/{id}")
    public ResponseEntity<SingleResponse<DefectCategoryResponseDTO>> getById(@PathVariable Long id) {
        log.info("REST request to get DefectCategory by ID: {}", id);
        return ResponseEntity.ok(defectCategoryService.getDefectCategoryById(id));
    }
    
    // ============ GET BY CODE ============
    @GetMapping("/code/{code}")
    public ResponseEntity<SingleResponse<DefectCategoryResponseDTO>> getByCode(@PathVariable String code) {
        log.info("REST request to get DefectCategory by code: {}", code);
        return ResponseEntity.ok(defectCategoryService.getDefectCategoryByCode(code));
    }
    
    // ============ GET ALL (paginated) ============
    @GetMapping("/v1/all")
    public ResponseEntity<SingleResponse<PageResponse<DefectCategoryResponseDTO>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "displayOrder") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir) {
        
        Pageable pageable = PageRequest.of(page, size,
            Sort.by(Sort.Direction.fromString(sortDir), sortBy));
        
        log.info("REST request to get all DefectCategories - page: {}, size: {}", page, size);
        return ResponseEntity.ok(defectCategoryService.getAllDefectCategories(pageable));
    }
    
    // ============ SEARCH (dynamic filters + pagination) ============
    @PostMapping("/search")
    public ResponseEntity<SingleResponse<PageResponse<DefectCategoryResponseDTO>>> search(
            @RequestBody DefectCategorySearchDTO searchDTO,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "displayOrder") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir) {
        
        Pageable pageable = PageRequest.of(page, size,
            Sort.by(Sort.Direction.fromString(sortDir), sortBy));
        
        log.info("REST request to search DefectCategories: {}", searchDTO);
        return ResponseEntity.ok(defectCategoryService.searchDefectCategories(searchDTO, pageable));
    }
}
package org.optipace.garmentService.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.optipace.garmentService.dto.request.AddDefectCategoryRequest;
import org.optipace.garmentService.dto.request.UpdateDefectCategoryRequest;
import org.optipace.garmentService.dto.response.DefectCategoryResponse;
import org.optipace.garmentService.dto.response.ListOfDefectCategoryResponse;
import org.optipace.garmentService.dto.response.PageResponse;
import org.optipace.garmentService.dto.response.SingleResponse;
import org.optipace.garmentService.service.DefectCategoryService;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/defectCategory")
@RequiredArgsConstructor
public class DefectCategoryController {

    private final DefectCategoryService defectCategoryService;

    @PostMapping("/v1/add")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<SingleResponse<?>> createDefectCategory(
            @Valid @RequestBody AddDefectCategoryRequest request,
            @RequestHeader("X-User-Id") String adminId) {

        return ResponseEntity.ok(
                defectCategoryService.createDefectCategory(
                        request,
                        adminId
                )
        );
    }

    @GetMapping("/v1/all")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<
            SingleResponse<PageResponse<ListOfDefectCategoryResponse>>>
            getAllDefectCategories(
                    @RequestParam(defaultValue = "0") int page,
                    @RequestParam(defaultValue = "10") int size) {

        Pageable pageable =
                PageRequest.of(page, size);

        return ResponseEntity.ok(
                defectCategoryService.getAllDefectCategories(
                        pageable
                )
        );
    }

    @GetMapping("/v1/{defectCategoryId}")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<SingleResponse<DefectCategoryResponse>>
            getDefectCategoryById(
                    @PathVariable Long defectCategoryId) {

        return ResponseEntity.ok(
                defectCategoryService.getDefectCategoryById(
                        defectCategoryId
                )
        );
    }

    @PatchMapping("/v1/update/{defectCategoryId}")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<SingleResponse<?>> updateDefectCategory(
            @PathVariable Long defectCategoryId,
            @Valid @RequestBody UpdateDefectCategoryRequest request,
            @RequestHeader("X-User-Id") String adminId) {

        return ResponseEntity.ok(
                defectCategoryService.updateDefectCategory(
                        defectCategoryId,
                        request,
                        adminId
                )
        );
    }

//    @DeleteMapping("/v1/delete/{defectCategoryId}")
//    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
//    public ResponseEntity<SingleResponse<?>> deleteDefectCategoryById(
//            @PathVariable Long defectCategoryId,
//            @RequestHeader("X-User-Id") String adminId) {
//
//        return ResponseEntity.ok(
//                defectCategoryService.deleteDefectCategoryById(
//                        defectCategoryId,
//                        adminId
//                )
//        );
//    }
}
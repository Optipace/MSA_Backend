package org.optipace.masterService.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.optipace.masterService.dto.request.SectionRequest;
import org.optipace.masterService.dto.request.SectionUpdateRequest;
import org.optipace.masterService.dto.response.*;
import org.optipace.masterService.service.SectionService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/section")
@RequiredArgsConstructor
public class SectionController {

    private final SectionService sectionService;

    @GetMapping("/v1/all")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<PageResponse<ListOfSectionResponse>>> getAllSection(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(sectionService.getAllSections(pageable));
    }

    @PostMapping("/v1/create")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<?>> createSection(@Valid @RequestBody SectionRequest request, @RequestHeader("X-User-Id") String factoryAdminId) {
        return ResponseEntity.ok(sectionService.createSection(request, factoryAdminId));
    }

    @PatchMapping("/v1/update/{sectionId}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<?>> updateSection(@PathVariable Long sectionId, @Valid @RequestBody SectionUpdateRequest request, @RequestHeader("X-User-Id") String factoryAdminId){
        return ResponseEntity.ok(sectionService.updateSection(sectionId, request, factoryAdminId));
    }

    @GetMapping("/v1/{id}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<SectionResponse>> getSectionById(@PathVariable Long id) {
        return ResponseEntity.ok(sectionService.getSectionById(id));
    }

    @DeleteMapping("/v1/{id}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<?>> deleteSection(@PathVariable Long id,
            @RequestHeader("X-User-Id") String adminId) {
        return ResponseEntity.ok(sectionService.deleteSectionById(id, adminId));
    }
}

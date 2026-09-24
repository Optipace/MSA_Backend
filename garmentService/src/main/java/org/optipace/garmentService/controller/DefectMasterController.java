package org.optipace.garmentService.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.optipace.garmentService.dto.request.AddDefectMasterRequest;
import org.optipace.garmentService.dto.request.UpdateDefectMasterRequest;
import org.optipace.garmentService.dto.response.DefectMasterResponse;
import org.optipace.garmentService.dto.response.ListOfDefectMasterResponse;
import org.optipace.garmentService.dto.response.PageResponse;
import org.optipace.garmentService.dto.response.SingleResponse;
import org.optipace.garmentService.service.DefectMasterService;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/defectMaster")
@RequiredArgsConstructor
public class DefectMasterController {

    private final DefectMasterService defectMasterService;

    @PostMapping("/v1/add")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<SingleResponse<?>> createDefectMaster(
            @Valid @RequestBody AddDefectMasterRequest request,
            @RequestHeader("X-User-Id") String adminId) {

        return ResponseEntity.ok(
                defectMasterService.createDefectMaster(
                        request,
                        adminId
                )
        );
    }

    @GetMapping("/v1/all")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<
            SingleResponse<PageResponse<ListOfDefectMasterResponse>>>
            getAllDefectMasters(
                    @RequestParam(defaultValue = "0") int page,
                    @RequestParam(defaultValue = "10") int size) {

        Pageable pageable =
                PageRequest.of(page, size);

        return ResponseEntity.ok(
                defectMasterService.getAllDefectMasters(
                        pageable
                )
        );
    }

    @GetMapping("/v1/{defectId}")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<SingleResponse<DefectMasterResponse>>
            getDefectMasterById(
                    @PathVariable Long defectId) {

        return ResponseEntity.ok(
                defectMasterService.getDefectMasterById(
                        defectId
                )
        );
    }

    @PatchMapping("/v1/update/{defectId}")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<SingleResponse<?>> updateDefectMaster(
            @PathVariable Long defectId,
            @Valid @RequestBody UpdateDefectMasterRequest request,
            @RequestHeader("X-User-Id") String adminId) {

        return ResponseEntity.ok(
                defectMasterService.updateDefectMaster(
                        defectId,
                        request,
                        adminId
                )
        );
    }

//    @DeleteMapping("/v1/delete/{defectId}")
//    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
//    public ResponseEntity<SingleResponse<?>> deleteDefectMasterById(
//            @PathVariable Long defectId,
//            @RequestHeader("X-User-Id") String adminId) {
//
//        return ResponseEntity.ok(
//                defectMasterService.deleteDefectMasterById(
//                        defectId,
//                        adminId
//                )
//        );
//    }
}
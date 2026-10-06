package org.optipace.garmentService.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.optipace.garmentService.dto.request.AddGarmentAreaRequest;
import org.optipace.garmentService.dto.request.UpdateGarmentAreaRequest;
import org.optipace.garmentService.dto.response.GarmentAreaResponse;
import org.optipace.garmentService.dto.response.ListOfGarmentAreaResponse;
import org.optipace.garmentService.dto.response.PageResponse;
import org.optipace.garmentService.dto.response.SingleResponse;
import org.optipace.garmentService.service.GarmentAreaService;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/garmentArea")
@RequiredArgsConstructor
public class GarmentAreaController {

    private final GarmentAreaService garmentAreaService;

    @PostMapping("/v1/add")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<SingleResponse<?>> createGarmentArea(
            @Valid @RequestBody AddGarmentAreaRequest request,
            @RequestHeader("X-User-Id") String adminId) {

        return ResponseEntity.ok(
                garmentAreaService.createGarmentArea(
                        request,
                        adminId
                )
        );
    }

    @GetMapping("/v1/all")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<
            SingleResponse<PageResponse<ListOfGarmentAreaResponse>>>
            getAllGarmentAreas(
                    @RequestParam(defaultValue = "0") int page,
                    @RequestParam(defaultValue = "10") int size) {

        Pageable pageable =
                PageRequest.of(page, size);

        return ResponseEntity.ok(
                garmentAreaService.getAllGarmentAreas(
                        pageable
                )
        );
    }

    @GetMapping("/v1/{garmentAreaId}")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<SingleResponse<GarmentAreaResponse>>
            getGarmentAreaById(
                    @PathVariable Long garmentAreaId) {

        return ResponseEntity.ok(
                garmentAreaService.getGarmentAreaById(
                        garmentAreaId
                )
        );
    }

    @PatchMapping("/v1/update/{garmentAreaId}")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<SingleResponse<?>> updateGarmentArea(
            @PathVariable Long garmentAreaId,
            @Valid @RequestBody UpdateGarmentAreaRequest request,
            @RequestHeader("X-User-Id") String adminId) {

        return ResponseEntity.ok(
                garmentAreaService.updateGarmentArea(
                        garmentAreaId,
                        request,
                        adminId
                )
        );
    }

//    @DeleteMapping("/v1/delete/{garmentAreaId}")
//    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
//    public ResponseEntity<SingleResponse<?>> deleteGarmentAreaById(
//            @PathVariable Long garmentAreaId,
//            @RequestHeader("X-User-Id") String adminId) {
//
//        return ResponseEntity.ok(
//                garmentAreaService.deleteGarmentAreaById(
//                        garmentAreaId,
//                        adminId
//                )
//        );
//    }
}
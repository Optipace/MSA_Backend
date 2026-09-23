package org.optipace.masterService.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.optipace.masterService.dto.request.AddCityRequest;
import org.optipace.masterService.dto.request.UpdateCityRequest;
import org.optipace.masterService.dto.response.*;
import org.optipace.masterService.service.CityService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/city")
@RequiredArgsConstructor
public class CityController {

    private final CityService cityService;

    @PostMapping("/v1/add")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<SingleResponse<?>> createCity(
            @Valid @RequestBody AddCityRequest request,
            @RequestHeader("X-User-Id") String adminId
    ) {

        return ResponseEntity.ok(
                cityService.createCity(
                        request,
                        adminId
                )
        );
    }

    @GetMapping("/v1/all")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<
            SingleResponse<PageResponse<ListOfCityResponse>>
            > getAllCity(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        Pageable pageable =
                PageRequest.of(page, size);

        return ResponseEntity.ok(
                cityService.getAllCity(pageable)
        );
    }

    @PatchMapping("/v1/update/{cityId}")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<SingleResponse<?>> updateCity(
            @PathVariable Long cityId,
            @Valid @RequestBody UpdateCityRequest request,
            @RequestHeader("X-User-Id") String adminId
    ) {

        return ResponseEntity.ok(
                cityService.updateCity(
                        cityId,
                        request,
                        adminId
                )
        );
    }

    @GetMapping("/v1/{cityId}")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<SingleResponse<CityResponse>> getCityById(
            @PathVariable Long cityId
    ) {

        return ResponseEntity.ok(
                cityService.getCityById(cityId)
        );
    }

    @DeleteMapping("/v1/delete/{cityId}")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<SingleResponse<?>> deleteCityById(
            @PathVariable Long cityId,
            @RequestHeader("X-User-Id") String adminId
    ) {

        return ResponseEntity.ok(
                cityService.deleteCityById(
                        cityId,
                        adminId
                )
        );
    }
}
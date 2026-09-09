package org.optipace.masterService.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.optipace.masterService.dto.request.FactoryRequest;
import org.optipace.masterService.dto.request.FactoryUpdateRequest;
import org.optipace.masterService.dto.response.ListOfFactoriesResponse;
import org.optipace.masterService.dto.response.FactoryResponse;
import org.optipace.masterService.dto.response.PageResponse;
import org.optipace.masterService.dto.response.SingleResponse;
import org.optipace.masterService.service.FactoryService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/factory")
@RequiredArgsConstructor
public class FactoryController {

    private final FactoryService factoryService;

    @GetMapping("/v1/all")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<PageResponse<ListOfFactoriesResponse>>> getAllFactories(@RequestParam(defaultValue = "0")int page, @RequestParam(defaultValue = "10")int size){
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(factoryService.getAllFactories(pageable));
    }

    @PostMapping("/v1/create")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<?>> createFactory(@Valid @RequestBody FactoryRequest request, @RequestHeader("X-User-Id") String factoryAdminId) {
        return ResponseEntity.ok(factoryService.createFactory(request, factoryAdminId));
    }

    @PatchMapping("/v1/update/{factoryId}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<?>> updateFactory(@PathVariable Long factoryId, @Valid @RequestBody FactoryUpdateRequest request, @RequestHeader("X-User-Id") String factoryAdminId){
        return ResponseEntity.ok(factoryService.updateFactory(factoryId, request, factoryAdminId));
    }

    @GetMapping("/v1/{factoryId}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<FactoryResponse>> getFactoryById(@PathVariable Long factoryId){
        return ResponseEntity.ok(factoryService.getFactoryById(factoryId));
    }

    @DeleteMapping("/v1/delete/{factoryId}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'FACTORY_ADMIN')")
    public ResponseEntity<SingleResponse<?>> deleteFactoryById(@PathVariable Long factoryId, @RequestHeader("X-User-Id") String factoryAdminId){
        return ResponseEntity.ok(factoryService.deleteFactoryById(factoryId, factoryAdminId));
    }
}

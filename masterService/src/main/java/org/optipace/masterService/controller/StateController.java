package org.optipace.masterService.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.optipace.masterService.dto.request.AddStateRequest;
import org.optipace.masterService.dto.request.UpdateStateRequest;
import org.optipace.masterService.dto.response.ListOfStateResponse;
import org.optipace.masterService.dto.response.PageResponse;
import org.optipace.masterService.dto.response.SingleResponse;
import org.optipace.masterService.dto.response.StateResponse;
import org.optipace.masterService.service.StateService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/state")
@RequiredArgsConstructor
public class StateController {

    private final StateService stateService;


    @PostMapping("/v1/add")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<SingleResponse<?>> createState(
            @Valid @RequestBody AddStateRequest request,
            @RequestHeader("X-User-Id") String adminId) {
        return ResponseEntity.ok(stateService.createState(request, adminId));
    }


    @GetMapping("/v1/all")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<SingleResponse<PageResponse<ListOfStateResponse>>> getAllState(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(stateService.getAllState(pageable));
    }

    @PatchMapping("/v1/update/{stateId}")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<SingleResponse<?>> updateState(
            @PathVariable Long stateId,
            @Valid @RequestBody UpdateStateRequest request,
            @RequestHeader("X-User-Id") String adminId) {
        return ResponseEntity.ok(stateService.updateState(stateId, request, adminId));
    }

    @GetMapping("/v1/{stateId}")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<SingleResponse<StateResponse>> getStateById(
            @PathVariable Long stateId) {
        return ResponseEntity.ok(stateService.getStateById(stateId));
    }

    @DeleteMapping("/v1/delete/{stateId}")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<SingleResponse<?>> deleteStateById(
            @PathVariable Long stateId,
            @RequestHeader("X-User-Id") String adminId) {
        return ResponseEntity.ok(stateService.deleteStateById(stateId, adminId));
    }
}

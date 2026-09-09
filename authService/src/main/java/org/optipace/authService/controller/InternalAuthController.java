package org.optipace.authService.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.optipace.authService.dto.requestDto.InternalRegisterLoginRequest;
import org.optipace.authService.dto.responseDto.SingleResponse;
import org.optipace.authService.service.InternalAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/internal")
@RequiredArgsConstructor
public class InternalAuthController {

    private final InternalAuthService internalAuthService;

    @PostMapping("/register-login")
    public ResponseEntity<SingleResponse<?>> registerEmployeeLogin(@Valid @RequestBody InternalRegisterLoginRequest request) {
        return ResponseEntity.ok(internalAuthService.registerEmployeeCredentials(request));
    }
}

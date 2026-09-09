package org.optipace.authService.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.optipace.authService.dto.requestDto.LoginRequest;
import org.optipace.authService.dto.requestDto.UpdatePasswordRequest;
import org.optipace.authService.dto.responseDto.SingleResponse;
import org.optipace.authService.dto.responseDto.TokenResponse;
import org.optipace.authService.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/v1/login")
    public ResponseEntity<SingleResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest httpServletrequest) {
        return ResponseEntity.ok(authService.login(loginRequest, httpServletrequest));
    }

    @PutMapping("/v1/updatePassword")
    public ResponseEntity<SingleResponse<?>> updatePassword(@Valid @RequestBody UpdatePasswordRequest request, @RequestHeader("X-User-Id") String employeeId){
        return ResponseEntity.ok(authService.updatePassword(request,employeeId));
    }
}

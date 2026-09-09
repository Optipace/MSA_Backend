package org.optipace.authService.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.optipace.authService.dto.requestDto.LoginRequest;
import org.optipace.authService.dto.requestDto.UpdatePasswordRequest;
import org.optipace.authService.dto.responseDto.SingleResponse;
import org.optipace.authService.dto.responseDto.TokenResponse;

public interface AuthService {
    SingleResponse<TokenResponse> login(LoginRequest loginRequest, HttpServletRequest httpServletRequest);

    SingleResponse<?> updatePassword(UpdatePasswordRequest request, String employeeId);
}

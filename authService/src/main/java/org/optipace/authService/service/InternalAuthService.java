package org.optipace.authService.service;

import org.optipace.authService.dto.requestDto.InternalRegisterLoginRequest;
import org.optipace.authService.dto.responseDto.SingleResponse;

public interface InternalAuthService {
    SingleResponse<?> registerEmployeeCredentials(InternalRegisterLoginRequest request);
}

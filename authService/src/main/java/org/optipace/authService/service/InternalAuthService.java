package org.optipace.authService.service;

import org.optipace.authService.dto.requestDto.InternalRegisterLoginRequest;
import org.optipace.authService.dto.responseDto.SingleResponse;

import java.util.List;
import java.util.Map;

public interface InternalAuthService {
    SingleResponse<?> registerEmployeeCredentials(InternalRegisterLoginRequest request);

    SingleResponse<Map<Long, Long>> getRoleIdsForEmployees(List<Long> employeeIds);
}

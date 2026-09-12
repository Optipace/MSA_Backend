package org.optipace.masterService.client;

import org.optipace.masterService.dto.request.EmployeeLoginDto;
import org.optipace.masterService.dto.response.SingleResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(name = "authService")
public interface AuthServiceClient {

    @PostMapping("/v1/internal/register-login")
    SingleResponse<?> registerEmployeeLogin(@RequestBody EmployeeLoginDto request);

    @PostMapping("/v1/internal/roles/batch")
    SingleResponse<Map<Long, Long>> getRoleIdsForEmployees(@RequestBody List<Long> employeeIds);
}

package com.msa.msa.client;

//import org.optipace.msaService.dto.DeductPointsRequest;
//import org.optipace.msaService.dto.DeductPointsResponse;
//import org.optipace.msaService.dto.SingleResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.msa.msa.scoring.dto.DeductPointsRequest;
import com.msa.msa.scoring.dto.DeductPointsResponse;
import com.msa.msa.scoring.dto.SingleResponse;


// The name must exactly match the spring.application.name of adminService
@FeignClient(name = "adminService") 
public interface AdminServiceClient {

    @PostMapping("/purchase/deduct")
    SingleResponse<DeductPointsResponse> deductPoints(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody DeductPointsRequest request
    );
}
package org.optipace.masterService.service;

import org.optipace.masterService.dto.request.AddStateRequest;
import org.optipace.masterService.dto.request.UpdateStateRequest;
import org.optipace.masterService.dto.response.ListOfStateResponse;
import org.optipace.masterService.dto.response.PageResponse;
import org.optipace.masterService.dto.response.SingleResponse;
import org.optipace.masterService.dto.response.StateResponse;
import org.springframework.data.domain.Pageable;

public interface StateService {

    SingleResponse<?> createState(
            AddStateRequest request,
            String adminId
    );

    SingleResponse<PageResponse<ListOfStateResponse>> getAllState(
            Pageable pageable
    );

    SingleResponse<?> updateState(
            Long stateId,
            UpdateStateRequest request,
            String adminId
    );

    SingleResponse<StateResponse> getStateById(
            Long stateId
    );

    SingleResponse<?> deleteStateById(
            Long stateId,
            String adminId
    );
}

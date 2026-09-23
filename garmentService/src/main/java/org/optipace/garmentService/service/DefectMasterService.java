package org.optipace.garmentService.service;

import org.optipace.garmentService.dto.request.AddDefectMasterRequest;
import org.optipace.garmentService.dto.request.UpdateDefectMasterRequest;
import org.optipace.garmentService.dto.response.DefectMasterResponse;
import org.optipace.garmentService.dto.response.ListOfDefectMasterResponse;
import org.optipace.garmentService.dto.response.PageResponse;
import org.optipace.garmentService.dto.response.SingleResponse;

import org.springframework.data.domain.Pageable;

public interface DefectMasterService {

    SingleResponse<?> createDefectMaster(
            AddDefectMasterRequest request,
            String adminId
    );

    SingleResponse<PageResponse<ListOfDefectMasterResponse>>
            getAllDefectMasters(Pageable pageable);

    SingleResponse<DefectMasterResponse>
            getDefectMasterById(Long defectId);

    SingleResponse<?> updateDefectMaster(
            Long defectId,
            UpdateDefectMasterRequest request,
            String adminId
    );

//    SingleResponse<?> deleteDefectMasterById(
//            Long defectId,
//            String adminId
//    );
}
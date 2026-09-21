package org.optipace.masterService.service;

import org.optipace.masterService.dto.request.AddShiftRequest;
import org.optipace.masterService.dto.request.UpdateShiftRequest;
import org.optipace.masterService.dto.response.ListOfShiftResponse;
import org.optipace.masterService.dto.response.PageResponse;
import org.optipace.masterService.dto.response.ShiftResponse;
import org.optipace.masterService.dto.response.SingleResponse;
import org.springframework.data.domain.Pageable;

public interface ShiftService {
	
	SingleResponse<?> createShift(AddShiftRequest request,String adminId);

    SingleResponse<PageResponse<ListOfShiftResponse>> getAllShift(Pageable pageable);

    SingleResponse<?> updateShift(Long shiftId,UpdateShiftRequest request,String adminId);

    SingleResponse<ShiftResponse> getShiftById(Long shiftId);

    SingleResponse<?> deleteShiftById(Long shiftId,String adminId);
	
}

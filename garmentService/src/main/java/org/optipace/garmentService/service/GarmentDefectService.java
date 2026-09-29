package org.optipace.garmentService.service;

import org.optipace.garmentService.dto.request.GarmentDefectSubmissionRequest;
import org.optipace.garmentService.dto.response.GarmentDefectSubmissionResponse;
import org.optipace.garmentService.dto.response.SingleResponse;

public interface GarmentDefectService {

    /**
     * Persists one garment inspection:
     *  - 1 row in garment_instance
     *  - N rows in garment_expected_defect (one per defect+area combination)
     *
     * @param request the payload built from the "Garment Identification" form
     * @param adminId value of the X-User-Id header (numeric user ID as string)
     * @return SingleResponse wrapping a summary of what was saved
     */
    SingleResponse<GarmentDefectSubmissionResponse> submitGarmentDefects(
            GarmentDefectSubmissionRequest request,
            String adminId);
}
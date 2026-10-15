package org.optipace.garmentService.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.optipace.garmentService.dto.request.GarmentAreaDefectRequest;
import org.optipace.garmentService.dto.request.GarmentAreaDefectUpdateRequest;
import org.optipace.garmentService.dto.response.GarmentAreaDefectResponse;
import org.optipace.garmentService.dto.response.ListOfGarmentAreaDefectResponse;
import org.optipace.garmentService.dto.response.PageResponse;
import org.optipace.garmentService.dto.response.SingleResponse;
import org.optipace.garmentService.entity.GarmentAreaDefect;
import org.optipace.garmentService.exception.BadRequestException;
import org.optipace.garmentService.exception.NotFoundException;
import org.optipace.garmentService.repository.GarmentAreaDefectRepository;
import org.optipace.garmentService.repository.GarmentBatchRepository;
import org.optipace.garmentService.repository.GarmentAreaRepository;
import org.optipace.garmentService.repository.DefectMasterRepository;
import org.optipace.garmentService.service.GarmentAreaDefectService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GarmentAreaDefectServiceImpl
        implements GarmentAreaDefectService {

    private final GarmentAreaDefectRepository garmentAreaDefectRepository;
    private final GarmentBatchRepository garmentBatchRepository;
    private final GarmentAreaRepository garmentAreaRepository;
    private final DefectMasterRepository defectMasterRepository;
    private final ModelMapper modelMapper;

    // Validate garment batch ID.
    private void validateGarmentBatchId(Long garmentBatchId) {

        if (garmentBatchId == null || garmentBatchId <= 0) {
            throw new BadRequestException(
                    "Garment batch ID is required and must be positive."
            );
        }

        if (!garmentBatchRepository.existsById(garmentBatchId)) {
            throw new NotFoundException(
                    "Garment batch not found with ID: " + garmentBatchId
            );
        }
    }

    // Validate garment area ID.
    private void validateGarmentAreaId(Long garmentAreaId) {

        if (garmentAreaId == null || garmentAreaId <= 0) {
            throw new BadRequestException(
                    "Garment area ID is required and must be positive."
            );
        }

        if (!garmentAreaRepository.existsById(garmentAreaId)) {
            throw new NotFoundException(
                    "Garment area not found with ID: " + garmentAreaId
            );
        }
    }

    // Validate defect ID.
    private void validateDefectId(Long defectId) {

        if (defectId == null || defectId <= 0) {
            throw new BadRequestException(
                    "Defect ID is required and must be positive."
            );
        }

        if (!defectMasterRepository.existsById(defectId)) {
            throw new NotFoundException(
                    "Defect not found with ID: " + defectId
            );
        }
    }

    // Validate all three IDs.
    private void validateReferences(
            Long garmentBatchId,
            Long garmentAreaId,
            Long defectId) {

        validateGarmentBatchId(garmentBatchId);
        validateGarmentAreaId(garmentAreaId);
        validateDefectId(defectId);
    }

    @Override
    @Transactional
    public SingleResponse<?> createGarmentAreaDefect(
            GarmentAreaDefectRequest request,
            String userId) {

        if (request == null) {
            throw new BadRequestException(
                    "Request body cannot be null."
            );
        }

        // 1. Validate all three referenced IDs.
        validateReferences(
                request.getGarmentBatchId(),
                request.getGarmentAreaId(),
                request.getDefectId()
        );

        // 2. Check duplicate mapping.
        boolean exists = garmentAreaDefectRepository
                .existsByGarmentBatchIdAndGarmentAreaIdAndDefectId(
                        request.getGarmentBatchId(),
                        request.getGarmentAreaId(),
                        request.getDefectId()
                );

        if (exists) {
            throw new BadRequestException(
                    "This defect is already mapped to the garment area in this batch."
            );
        }

        // 3. Create entity.
        GarmentAreaDefect entity = new GarmentAreaDefect();

        entity.setGarmentBatchId(request.getGarmentBatchId());
        entity.setGarmentAreaId(request.getGarmentAreaId());
        entity.setDefectId(request.getDefectId());
        entity.setSeverityLevelId(request.getSeverityLevelId());

        entity.setMaxDeductionMarks(
                request.getMaxDeductionMarks() != null
                        ? request.getMaxDeductionMarks()
                        : BigDecimal.ONE
        );

        entity.setIsMandatory(
                request.getIsMandatory() != null
                        ? request.getIsMandatory()
                        : false
        );

        entity.setAiConfidenceThreshold(
                request.getAiConfidenceThreshold()
        );

        entity.setRemarks(request.getRemarks());
        entity.setCreatedBy(Long.parseLong(userId));
        entity.setRecordStatus('A');

        // 4. Save entity.
        GarmentAreaDefect saved =
                garmentAreaDefectRepository.save(entity);

        GarmentAreaDefectResponse response =
                modelMapper.map(
                        saved,
                        GarmentAreaDefectResponse.class
                );

        return SingleResponse.success(
                response,
                "Garment area defect successfully created"
        );
    }

    @Override
    @Transactional(readOnly = true)
    public SingleResponse<GarmentAreaDefectResponse>
            getGarmentAreaDefectById(Long garmentAreaDefectId) {

        GarmentAreaDefect entity =
                garmentAreaDefectRepository.findById(garmentAreaDefectId)
                        .orElseThrow(() -> new NotFoundException(
                                "Garment area defect not found"
                        ));

        GarmentAreaDefectResponse response =
                modelMapper.map(
                        entity,
                        GarmentAreaDefectResponse.class
                );

        return SingleResponse.success(response);
    }

    @Override
    @Transactional(readOnly = true)
    public SingleResponse<PageResponse<ListOfGarmentAreaDefectResponse>>
            getAllGarmentAreaDefects(Pageable pageable) {

        Page<GarmentAreaDefect> page =
                garmentAreaDefectRepository.findByRecordStatusIn(
                        List.of('A', 'U'),
                        pageable
                );

        List<ListOfGarmentAreaDefectResponse> content =
                page.getContent()
                        .stream()
                        .map(entity -> modelMapper.map(
                                entity,
                                ListOfGarmentAreaDefectResponse.class
                        ))
                        .toList();

        PageResponse<ListOfGarmentAreaDefectResponse> pageResponse =
                new PageResponse<>();

        pageResponse.setContent(content);
        pageResponse.setTotalElements(page.getTotalElements());
        pageResponse.setTotalPages(page.getTotalPages());

        return SingleResponse.success(pageResponse);
    }
   

    @Override
    @Transactional
    public SingleResponse<?> updateGarmentAreaDefect(
            Long garmentAreaDefectId,
            GarmentAreaDefectUpdateRequest request,
            String userId) {

        if (request == null) {
            throw new BadRequestException(
                    "Request body cannot be null."
            );
        }

        // 1. Find existing record
        GarmentAreaDefect entity =
                garmentAreaDefectRepository.findById(garmentAreaDefectId)
                        .orElseThrow(() -> new NotFoundException(
                                "Garment area defect not found"
                        ));

        // 2. Resolve final values
        Long garmentBatchId =
                request.getGarmentBatchId() != null
                        ? request.getGarmentBatchId()
                        : entity.getGarmentBatchId();

        Long garmentAreaId =
                request.getGarmentAreaId() != null
                        ? request.getGarmentAreaId()
                        : entity.getGarmentAreaId();

        Long defectId =
                request.getDefectId() != null
                        ? request.getDefectId()
                        : entity.getDefectId();

        // 3. Validate references
        validateReferences(
                garmentBatchId,
                garmentAreaId,
                defectId
        );

        // 4. Check duplicate mapping
        boolean combinationChanged =
                !Objects.equals(
                        garmentBatchId,
                        entity.getGarmentBatchId()
                )
                || !Objects.equals(
                        garmentAreaId,
                        entity.getGarmentAreaId()
                )
                || !Objects.equals(
                        defectId,
                        entity.getDefectId()
                );

        if (combinationChanged) {
            boolean exists = garmentAreaDefectRepository
                    .existsByGarmentBatchIdAndGarmentAreaIdAndDefectIdAndGarmentAreaDefectIdNot(
                            garmentBatchId,
                            garmentAreaId,
                            defectId,
                            garmentAreaDefectId
                    );

            if (exists) {
                throw new BadRequestException(
                        "This defect is already mapped to the garment area in this batch."
                );
            }
        }

        // 5. Update IDs
        entity.setGarmentBatchId(garmentBatchId);
        entity.setGarmentAreaId(garmentAreaId);
        entity.setDefectId(defectId);

        // 6. Update remaining fields when supplied
        if (request.getSeverityLevelId() != null) {
            entity.setSeverityLevelId(
                    request.getSeverityLevelId()
            );
        }

        if (request.getMaxDeductionMarks() != null) {
            entity.setMaxDeductionMarks(
                    request.getMaxDeductionMarks()
            );
        }

        if (request.getIsMandatory() != null) {
            entity.setIsMandatory(
                    request.getIsMandatory()
            );
        }

        if (request.getAiConfidenceThreshold() != null) {
            entity.setAiConfidenceThreshold(
                    request.getAiConfidenceThreshold()
            );
        }

     // Preserve remarks unless new remarks are provided
        if (request.getRemarks() != null) {
            entity.setRemarks(request.getRemarks());
        }

        // Set record status to U after update
        entity.setRecordStatus('U');

        // 8. Set updated-by user
        entity.setUpdatedBy(Long.parseLong(userId));

        // 9. Save updated record
        GarmentAreaDefect saved =
                garmentAreaDefectRepository.save(entity);

        GarmentAreaDefectResponse response =
                modelMapper.map(
                        saved,
                        GarmentAreaDefectResponse.class
                );

        return SingleResponse.success(
                response,
                "Garment area defect successfully updated"
        );
    }

//    @Override
//    @Transactional
//    public SingleResponse<?> deleteGarmentAreaDefect(
//            Long garmentAreaDefectId,
//            String userId) {
//
//        GarmentAreaDefect entity =
//                garmentAreaDefectRepository.findById(garmentAreaDefectId)
//                        .orElseThrow(() -> new NotFoundException(
//                                "Garment area defect not found"
//                        ));
//
//        // Soft delete.
//        entity.setRecordStatus('D');
//        entity.setUpdatedBy(Long.parseLong(userId));
//
//        garmentAreaDefectRepository.save(entity);
//
//        return SingleResponse.success(
//                null,
//                "Garment area defect successfully deleted"
//        );
//    }
}

package org.optipace.garmentService.service.impl;

import java.util.List;

import org.optipace.garmentService.dto.request.AddGarmentTypeRequest;
import org.optipace.garmentService.dto.request.UpdateGarmentTypeRequest;
import org.optipace.garmentService.dto.response.GarmentTypeResponse;
import org.optipace.garmentService.dto.response.ListOfGarmentTypeResponse;
import org.optipace.garmentService.dto.response.PageResponse;
import org.optipace.garmentService.dto.response.SingleResponse;
import org.optipace.garmentService.entity.GarmentType;
import org.optipace.garmentService.enums.CustomStatus;
import org.optipace.garmentService.exception.BadRequestException;
import org.optipace.garmentService.exception.NotFoundException;
import org.optipace.garmentService.repository.GarmentTypeRepository;
import org.optipace.garmentService.service.GarmentTypeService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class GarmentTypeServiceImpl implements GarmentTypeService {

    private final GarmentTypeRepository garmentTypeRepository;

    @Override
    @Transactional
    public SingleResponse<?> createGarmentType(
            AddGarmentTypeRequest request,
            String adminId) {

        log.info(
                "Initiating garment type creation for code: {} by Admin: {}",
                request.getGarmentCode(),
                adminId
        );

        // Validate product category ID
        if (request.getProductCategoryId() == null) {

            throw new BadRequestException(
                    "Product category ID is required"
            );
        }

        // Validate garment code
        if (request.getGarmentCode() == null
                || request.getGarmentCode().trim().isEmpty()) {

            throw new BadRequestException(
                    "Garment code is required"
            );
        }

        // Validate garment name
        if (request.getGarmentName() == null
                || request.getGarmentName().trim().isEmpty()) {

            throw new BadRequestException(
                    "Garment name is required"
            );
        }

        String garmentCode = request.getGarmentCode()
                .trim()
                .toUpperCase();

        
     // Check duplicate among ALL records (A and D)
        if (garmentTypeRepository.existsByGarmentCode(garmentCode)) {

            log.warn(
                    "Garment type already exists with code: {}",
                    garmentCode
            );

            throw new BadRequestException(
                    "Garment type with code "
                            + garmentCode
                            + " already exists"
            );
        }

        try {

            GarmentType garmentType = new GarmentType();

            garmentType.setProductCategoryId(
                    request.getProductCategoryId()
            );

            garmentType.setGarmentCode(
                    garmentCode
            );

            garmentType.setGarmentName(
                    request.getGarmentName().trim()
            );

            garmentType.setDescription(
                    request.getDescription()
            );

            garmentType.setRemarks(
                    request.getRemarks()
            );

            if (adminId != null && !adminId.trim().isEmpty()) {

                garmentType.setCreatedBy(
                        Long.parseLong(adminId)
                );
            }

            garmentType.setVersionNo(1);
            garmentType.setRecordStatus('A');

            GarmentType saved =
                    garmentTypeRepository.save(garmentType);

            log.info(
                    "Garment type successfully created with ID: {}",
                    saved.getGarmentTypeId()
            );

            return SingleResponse.success(
                    "Garment type created successfully with code: "
                            + saved.getGarmentCode()
            );

        } catch (DataIntegrityViolationException ex) {

            log.warn(
                    "Duplicate garment type code detected: {}",
                    garmentCode
            );

            throw new BadRequestException(
                    "Garment type with code "
                            + garmentCode
                            + " already exists"
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public SingleResponse<PageResponse<ListOfGarmentTypeResponse>>
            getAllGarmentTypes(Pageable pageable) {

        Pageable sortedPageable = pageable.getSort().isSorted()
                ? pageable
                : PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        Sort.by(
                                Sort.Order.asc("garmentName")
                                        .nullsLast()
                        )
                );

        Page<GarmentType> garmentTypePage =
                garmentTypeRepository.findByRecordStatus(
                        "A",
                        sortedPageable
                );

        List<ListOfGarmentTypeResponse> responseList =
                garmentTypePage.getContent()
                        .stream()
                        .map(garmentType ->
                                new ListOfGarmentTypeResponse(
                                        garmentType.getGarmentTypeId(),
                                        garmentType.getProductCategoryId(),
                                        garmentType.getGarmentCode(),
                                        garmentType.getGarmentName(),
                                        garmentType.getDescription()
                                )
                        )
                        .toList();

        PageResponse<ListOfGarmentTypeResponse> pageResponse =
                new PageResponse<>(
                        responseList,
                        garmentTypePage.getNumber(),
                        garmentTypePage.getSize(),
                        garmentTypePage.getTotalElements(),
                        garmentTypePage.getTotalPages(),
                        garmentTypePage.isLast(),
                        garmentTypePage.isFirst(),
                        garmentTypePage.getNumberOfElements(),
                        garmentTypePage.isEmpty()
                );

        return SingleResponse.success(pageResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public SingleResponse<GarmentTypeResponse>
            getGarmentTypeById(Long garmentTypeId) {

        GarmentType garmentType =
                garmentTypeRepository
                        .findByGarmentTypeIdAndRecordStatus(
                                garmentTypeId,
                                "A"
                        )
                        .orElseThrow(() ->
                                new NotFoundException(
                                        "Active garment type not found with ID: "
                                                + garmentTypeId
                                )
                        );

        GarmentTypeResponse response =
                mapToResponse(garmentType);

        return SingleResponse.success(response);
    }

    @Override
    @Transactional
    public SingleResponse<?> updateGarmentType(
            Long garmentTypeId,
            UpdateGarmentTypeRequest request,
            String adminId) {

        log.info(
                "Initiating garment type update for ID: {} by Admin: {}",
                garmentTypeId,
                adminId
        );

        GarmentType garmentType =
                garmentTypeRepository
                        .findByGarmentTypeIdAndRecordStatus(
                                garmentTypeId,
                                "A"
                        )
                        .orElseThrow(() ->
                                new NotFoundException(
                                        "Active garment type not found with ID: "
                                                + garmentTypeId
                                )
                        );

        // Validate product category ID
        if (request.getProductCategoryId() == null) {
            throw new BadRequestException(
                    "Product category ID is required"
            );
        }

        // Validate garment code
        if (request.getGarmentCode() == null
                || request.getGarmentCode().trim().isEmpty()) {

            throw new BadRequestException(
                    "Garment code is required"
            );
        }

        // Validate garment name
        if (request.getGarmentName() == null
                || request.getGarmentName().trim().isEmpty()) {

            throw new BadRequestException(
                    "Garment name is required"
            );
        }

        String garmentCode = request.getGarmentCode()
                .trim()
                .toUpperCase();

        // Check duplicate among ACTIVE records
        // Excluding current garment type
        if (garmentTypeRepository
                .existsByGarmentCodeAndGarmentTypeIdNotAndRecordStatus(
                        garmentCode,
                        garmentTypeId,
                        "A")) {

            log.warn(
                    "Garment type code already exists: {}",
                    garmentCode
            );

            throw new BadRequestException(
                    "Garment type with code "
                            + garmentCode
                            + " already exists"
            );
        }

        garmentType.setProductCategoryId(
                request.getProductCategoryId()
        );

        garmentType.setGarmentCode(
                garmentCode
        );

        garmentType.setGarmentName(
                request.getGarmentName().trim()
        );

        if (request.getDescription() != null) {
            garmentType.setDescription(
                    request.getDescription()
            );
        }

        if (request.getRemarks() != null) {
            garmentType.setRemarks(
                    request.getRemarks()
            );
        }

        if (adminId != null && !adminId.trim().isEmpty()) {
            garmentType.setUpdatedBy(
                    Long.parseLong(adminId)
            );
        }

        garmentTypeRepository.save(garmentType);

        log.info(
                "Garment type successfully updated with ID: {}",
                garmentTypeId
        );

        return SingleResponse.success("Garment type updated successfully");

    }

    @Override
    @Transactional
    public SingleResponse<?> deleteGarmentTypeById(
            Long garmentTypeId,
            String adminId) {

        log.info(
                "Initiating garment type deletion for ID: {} by Admin: {}",
                garmentTypeId,
                adminId
        );

        GarmentType garmentType =
                garmentTypeRepository
                        .findByGarmentTypeIdAndRecordStatus(
                                garmentTypeId,
                                "A"
                        )
                        .orElseThrow(() ->
                                new NotFoundException(
                                        "Active garment type not found with ID: "
                                                + garmentTypeId
                                )
                        );

        // Soft delete
        garmentType.setRecordStatus('D');

        if (adminId != null && !adminId.trim().isEmpty()) {
            garmentType.setUpdatedBy(
                    Long.parseLong(adminId)
            );
        }

        garmentTypeRepository.save(garmentType);

        log.info(
                "Garment type successfully deleted with ID: {}",
                garmentTypeId
        );

        return SingleResponse.success("Garment type deleted successfully");
    }

    private GarmentTypeResponse mapToResponse(
            GarmentType garmentType) {

        return new GarmentTypeResponse(
                garmentType.getGarmentTypeId(),
                garmentType.getProductCategoryId(),
                garmentType.getGarmentCode(),
                garmentType.getGarmentName(),
                garmentType.getDescription()
        );
    }
}
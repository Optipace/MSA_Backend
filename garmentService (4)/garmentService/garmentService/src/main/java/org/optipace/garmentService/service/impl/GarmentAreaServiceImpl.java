package org.optipace.garmentService.service.impl;

import java.util.List;

import org.optipace.garmentService.dto.request.AddGarmentAreaRequest;
import org.optipace.garmentService.dto.request.UpdateGarmentAreaRequest;
import org.optipace.garmentService.dto.response.GarmentAreaResponse;
import org.optipace.garmentService.dto.response.ListOfGarmentAreaResponse;
import org.optipace.garmentService.dto.response.PageResponse;
import org.optipace.garmentService.dto.response.SingleResponse;
import org.optipace.garmentService.entity.GarmentArea;
import org.optipace.garmentService.enums.CustomStatus;
import org.optipace.garmentService.exception.BadRequestException;
import org.optipace.garmentService.exception.NotFoundException;
import org.optipace.garmentService.repository.GarmentAreaRepository;
import org.optipace.garmentService.service.GarmentAreaService;

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
public class GarmentAreaServiceImpl implements GarmentAreaService {

    private final GarmentAreaRepository garmentAreaRepository;

    @Override
    @Transactional
    public SingleResponse<?> createGarmentArea(
            AddGarmentAreaRequest request,
            String adminId) {

        log.info(
                "Initiating garment area creation for code: {} by Admin: {}",
                request.getAreaCode(),
                adminId
        );

        if (request.getGarmentTypeId() == null) {
            throw new BadRequestException(
                    "Garment type ID is required"
            );
        }

        if (request.getAreaCode() == null
                || request.getAreaCode().trim().isEmpty()) {

            throw new BadRequestException(
                    "Area code is required"
            );
        }

        if (request.getAreaName() == null
                || request.getAreaName().trim().isEmpty()) {

            throw new BadRequestException(
                    "Area name is required"
            );
        }

        String areaCode = request.getAreaCode()
                .trim()
                .toUpperCase();

        if (garmentAreaRepository
                .existsByGarmentTypeIdAndAreaCode(
                        request.getGarmentTypeId(),
                        areaCode)) {

            log.warn(
                    "Garment area already exists with type ID: {} and code: {}",
                    request.getGarmentTypeId(),
                    areaCode
            );

            throw new BadRequestException(
                    "Garment area with code "
                            + areaCode
                            + " already exists for this garment type"
            );
        }

        GarmentArea garmentArea = new GarmentArea();

        garmentArea.setGarmentTypeId(
                request.getGarmentTypeId()
        );

        garmentArea.setAreaCode(
                areaCode
        );

        garmentArea.setAreaName(
                request.getAreaName().trim()
        );

        garmentArea.setDisplayOrder(
                request.getDisplayOrder()
        );

        garmentArea.setXCoordinate(
                request.getXCoordinate()
        );

        garmentArea.setYCoordinate(
                request.getYCoordinate()
        );

        garmentArea.setWidth(
                request.getWidth()
        );

        garmentArea.setHeight(
                request.getHeight()
        );

        GarmentArea saved =
                garmentAreaRepository.save(garmentArea);

        log.info(
                "Garment area successfully created with ID: {}",
                saved.getGarmentAreaId()
        );

        return new SingleResponse<>(
                "Garment area created successfully with code: "
                        + saved.getAreaCode(),
                CustomStatus.SUCCESS
        );
    }

    @Override
    @Transactional(readOnly = true)
    public SingleResponse<PageResponse<ListOfGarmentAreaResponse>>
            getAllGarmentAreas(Pageable pageable) {

        Pageable sortedPageable = pageable.getSort().isSorted()
                ? pageable
                : PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        Sort.by(
                                Sort.Order.asc("areaName")
                                        .nullsLast()
                        )
                );

        Page<GarmentArea> garmentAreaPage =
                garmentAreaRepository.findAll(
                        sortedPageable
                );

        List<ListOfGarmentAreaResponse> responseList =
                garmentAreaPage.getContent()
                        .stream()
                        .map(garmentArea ->
                                new ListOfGarmentAreaResponse(
                                        garmentArea.getGarmentAreaId(),
                                        garmentArea.getGarmentTypeId(),
                                        garmentArea.getAreaCode(),
                                        garmentArea.getAreaName(),
                                        garmentArea.getDisplayOrder(),
                                        garmentArea.getXCoordinate(),
                                        garmentArea.getYCoordinate(),
                                        garmentArea.getWidth(),
                                        garmentArea.getHeight()
                                )
                        )
                        .toList();

        PageResponse<ListOfGarmentAreaResponse> pageResponse =
                new PageResponse<>(
                        responseList,
                        garmentAreaPage.getNumber(),
                        garmentAreaPage.getSize(),
                        garmentAreaPage.getTotalElements(),
                        garmentAreaPage.getTotalPages(),
                        garmentAreaPage.isLast()
                );

        return new SingleResponse<>(
                pageResponse,
                CustomStatus.SUCCESS
        );
    }

    @Override
    @Transactional(readOnly = true)
    public SingleResponse<GarmentAreaResponse>
            getGarmentAreaById(Long garmentAreaId) {

        GarmentArea garmentArea =
                garmentAreaRepository
                        .findByGarmentAreaId(garmentAreaId)
                        .orElseThrow(() ->
                                new NotFoundException(
                                        "Garment area not found with ID: "
                                                + garmentAreaId
                                )
                        );

        GarmentAreaResponse response =
                mapToResponse(garmentArea);

        return new SingleResponse<>(
                response,
                CustomStatus.SUCCESS
        );
    }

    @Override
    @Transactional
    public SingleResponse<?> updateGarmentArea(
            Long garmentAreaId,
            UpdateGarmentAreaRequest request,
            String adminId) {

        log.info(
                "Initiating garment area update for ID: {} by Admin: {}",
                garmentAreaId,
                adminId
        );

        GarmentArea garmentArea =
                garmentAreaRepository
                        .findByGarmentAreaId(garmentAreaId)
                        .orElseThrow(() ->
                                new NotFoundException(
                                        "Garment area not found with ID: "
                                                + garmentAreaId
                                )
                        );

        if (request.getGarmentTypeId() == null) {
            throw new BadRequestException(
                    "Garment type ID is required"
            );
        }

        if (request.getAreaCode() == null
                || request.getAreaCode().trim().isEmpty()) {

            throw new BadRequestException(
                    "Area code is required"
            );
        }

        if (request.getAreaName() == null
                || request.getAreaName().trim().isEmpty()) {

            throw new BadRequestException(
                    "Area name is required"
            );
        }

        String areaCode = request.getAreaCode()
                .trim()
                .toUpperCase();

        if (garmentAreaRepository
                .existsByGarmentTypeIdAndAreaCodeAndGarmentAreaIdNot(
                        request.getGarmentTypeId(),
                        areaCode,
                        garmentAreaId)) {

            log.warn(
                    "Garment area code already exists: {}",
                    areaCode
            );

            throw new BadRequestException(
                    "Garment area with code "
                            + areaCode
                            + " already exists for this garment type"
            );
        }

        garmentArea.setGarmentTypeId(
                request.getGarmentTypeId()
        );

        garmentArea.setAreaCode(
                areaCode
        );

        garmentArea.setAreaName(
                request.getAreaName().trim()
        );

        garmentArea.setDisplayOrder(
                request.getDisplayOrder()
        );

        garmentArea.setXCoordinate(
                request.getXCoordinate()
        );

        garmentArea.setYCoordinate(
                request.getYCoordinate()
        );

        garmentArea.setWidth(
                request.getWidth()
        );

        garmentArea.setHeight(
                request.getHeight()
        );

        garmentAreaRepository.save(garmentArea);

        log.info(
                "Garment area successfully updated with ID: {}",
                garmentAreaId
        );

        return new SingleResponse<>(
                "Garment area updated successfully",
                CustomStatus.SUCCESS
        );
    }

//    @Override
//    @Transactional
//    public SingleResponse<?> deleteGarmentAreaById(
//            Long garmentAreaId,
//            String adminId) {
//
//        log.info(
//                "Initiating garment area deletion for ID: {} by Admin: {}",
//                garmentAreaId,
//                adminId
//        );
//
//        GarmentArea garmentArea =
//                garmentAreaRepository
//                        .findByGarmentAreaId(garmentAreaId)
//                        .orElseThrow(() ->
//                                new NotFoundException(
//                                        "Garment area not found with ID: "
//                                                + garmentAreaId
//                                )
//                        );
//
//        garmentAreaRepository.delete(garmentArea);
//
//        log.info(
//                "Garment area successfully deleted with ID: {}",
//                garmentAreaId
//        );
//
//        return new SingleResponse<>(
//                "Garment area deleted successfully",
//                CustomStatus.SUCCESS
//        );
//    }

    private GarmentAreaResponse mapToResponse(
            GarmentArea garmentArea) {

        return new GarmentAreaResponse(
                garmentArea.getGarmentAreaId(),
                garmentArea.getGarmentTypeId(),
                garmentArea.getAreaCode(),
                garmentArea.getAreaName(),
                garmentArea.getDisplayOrder(),
                garmentArea.getXCoordinate(),
                garmentArea.getYCoordinate(),
                garmentArea.getWidth(),
                garmentArea.getHeight()
        );
    }
}
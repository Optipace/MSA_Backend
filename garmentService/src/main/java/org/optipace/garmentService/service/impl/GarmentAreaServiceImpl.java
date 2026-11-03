
package org.optipace.garmentService.service.impl;

import java.util.List;

import org.optipace.garmentService.dto.request.AddGarmentAreaRequest;
import org.optipace.garmentService.dto.request.UpdateGarmentAreaRequest;
import org.optipace.garmentService.dto.response.GarmentAreaResponse;
import org.optipace.garmentService.dto.response.ListOfGarmentAreaResponse;
import org.optipace.garmentService.dto.response.SingleResponse;
import org.optipace.garmentService.entity.GarmentArea;
import org.optipace.garmentService.exception.BadRequestException;
import org.optipace.garmentService.exception.NotFoundException;
import org.optipace.garmentService.repository.GarmentAreaRepository;
import org.optipace.garmentService.service.GarmentAreaService;

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

        if (request.getAreaCode() == null
                || request.getAreaCode().trim().isEmpty()) {

            throw new BadRequestException("Area code is required");
        }

        if (request.getAreaName() == null
                || request.getAreaName().trim().isEmpty()) {

            throw new BadRequestException("Area name is required");
        }

        String areaCode = request.getAreaCode()
                .trim()
                .toUpperCase();

        if (garmentAreaRepository.existsByAreaCode(areaCode)) {

            log.warn(
                    "Garment area already exists with code: {}",
                    areaCode
            );

            throw new BadRequestException(
                    "Garment area with code "
                            + areaCode
                            + " already exists"
            );
        }

        GarmentArea garmentArea = new GarmentArea();

        garmentArea.setAreaCode(areaCode);

        garmentArea.setAreaName(
                request.getAreaName().trim()
        );

        garmentArea.setDisplayOrder(
                request.getDisplayOrder()
        );

        GarmentArea saved =
                garmentAreaRepository.save(garmentArea);

        log.info(
                "Garment area successfully created with ID: {}",
                saved.getGarmentAreaId()
        );

        return SingleResponse.success(
                "Garment area created successfully with code: "
                        + saved.getAreaCode()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public SingleResponse<List<ListOfGarmentAreaResponse>> getAllGarmentAreas() {

        List<GarmentArea> garmentAreas =
                garmentAreaRepository.findAll(
                        Sort.by(
                                Sort.Order.asc("areaName").nullsLast()
                        )
                );

        List<ListOfGarmentAreaResponse> responseList =
                garmentAreas.stream()
                        .map(garmentArea ->
                                new ListOfGarmentAreaResponse(
                                        garmentArea.getGarmentAreaId(),
                                        garmentArea.getAreaCode(),
                                        garmentArea.getAreaName(),
                                        garmentArea.getDisplayOrder()
                                )
                        )
                        .toList();

        return SingleResponse.success(responseList);
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

        return SingleResponse.success(response);
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

        if (request.getAreaCode() == null
                || request.getAreaCode().trim().isEmpty()) {

            throw new BadRequestException("Area code is required");
        }

        if (request.getAreaName() == null
                || request.getAreaName().trim().isEmpty()) {

            throw new BadRequestException("Area name is required");
        }

        String areaCode = request.getAreaCode()
                .trim()
                .toUpperCase();

        if (garmentAreaRepository
                .existsByAreaCodeAndGarmentAreaIdNot(
                        areaCode,
                        garmentAreaId)) {

            log.warn(
                    "Garment area code already exists: {}",
                    areaCode
            );

            throw new BadRequestException(
                    "Garment area with code "
                            + areaCode
                            + " already exists"
            );
        }

        garmentArea.setAreaCode(areaCode);

        garmentArea.setAreaName(
                request.getAreaName().trim()
        );

        garmentArea.setDisplayOrder(
                request.getDisplayOrder()
        );

        garmentAreaRepository.save(garmentArea);

        log.info(
                "Garment area successfully updated with ID: {}",
                garmentAreaId
        );

        return SingleResponse.success(
                "Garment area updated successfully"
        );
    }

    private GarmentAreaResponse mapToResponse(
            GarmentArea garmentArea) {

        return new GarmentAreaResponse(
                garmentArea.getGarmentAreaId(),
                garmentArea.getAreaCode(),
                garmentArea.getAreaName(),
                garmentArea.getDisplayOrder()
        );
    }
}

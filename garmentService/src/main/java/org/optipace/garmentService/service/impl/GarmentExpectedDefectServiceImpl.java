package org.optipace.garmentService.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.optipace.garmentService.dto.request.GarmentExpectedDefectRequest;
import org.optipace.garmentService.dto.request.GarmentExpectedDefectUpdateRequest;
import org.optipace.garmentService.dto.response.GarmentExpectedDefectResponse;
import org.optipace.garmentService.dto.response.ListOfGarmentExpectedDefectResponse;
import org.optipace.garmentService.dto.response.PageResponse;
import org.optipace.garmentService.dto.response.SingleResponse;
import org.optipace.garmentService.entity.GarmentExpectedDefect;
import org.optipace.garmentService.exception.BadRequestException;
import org.optipace.garmentService.exception.NotFoundException;
import org.optipace.garmentService.repository.GarmentExpectedDefectRepository;
import org.optipace.garmentService.service.GarmentExpectedDefectService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GarmentExpectedDefectServiceImpl
        implements GarmentExpectedDefectService {

    private final GarmentExpectedDefectRepository garmentExpectedDefectRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public SingleResponse<?> createGarmentExpectedDefect(
            GarmentExpectedDefectRequest request,
            String userId) {

        boolean exists =
                garmentExpectedDefectRepository
                        .existsByGarmentInstanceIdAndGarmentAreaIdAndDefectId(
                                request.getGarmentInstanceId(),
                                request.getGarmentAreaId(),
                                request.getDefectId()
                        );

        if (exists) {
            throw new BadRequestException(
                    "Garment expected defect already exists."
            );
        }

        GarmentExpectedDefect entity = new GarmentExpectedDefect();

        entity.setGarmentInstanceId(request.getGarmentInstanceId());
        entity.setGarmentAreaId(request.getGarmentAreaId());
        entity.setDefectId(request.getDefectId());
        entity.setSeverityLevelId(request.getSeverityLevelId());

        GarmentExpectedDefect saved =
                garmentExpectedDefectRepository.save(entity);

        GarmentExpectedDefectResponse response =
                modelMapper.map(
                        saved,
                        GarmentExpectedDefectResponse.class
                );

        return SingleResponse.success(
                response,
                "Garment expected defect successfully created"
        );
    }

    @Override
    public SingleResponse<GarmentExpectedDefectResponse>
    getGarmentExpectedDefectById(UUID garmentExpectedDefectId) {

        GarmentExpectedDefect entity =
                garmentExpectedDefectRepository
                        .findById(garmentExpectedDefectId)
                        .orElseThrow(() ->
                                new NotFoundException(
                                        "Garment expected defect not found"
                                )
                        );

        GarmentExpectedDefectResponse response =
                modelMapper.map(
                        entity,
                        GarmentExpectedDefectResponse.class
                );

        return SingleResponse.success(response);
    }

    @Override
    @Transactional
    public SingleResponse<?> updateGarmentExpectedDefect(
            UUID garmentExpectedDefectId,
            GarmentExpectedDefectUpdateRequest request) {

        GarmentExpectedDefect entity =
                garmentExpectedDefectRepository
                        .findById(garmentExpectedDefectId)
                        .orElseThrow(() ->
                                new NotFoundException(
                                        "Garment expected defect not found"
                                )
                        );

        Long garmentAreaId =
                request.getGarmentAreaId() != null
                        ? request.getGarmentAreaId()
                        : entity.getGarmentAreaId();

        Long defectId =
                request.getDefectId() != null
                        ? request.getDefectId()
                        : entity.getDefectId();

        boolean combinationChanged =
                !garmentAreaId.equals(entity.getGarmentAreaId())
                        || !defectId.equals(entity.getDefectId());

        if (combinationChanged) {

            boolean exists =
                    garmentExpectedDefectRepository
                            .existsByGarmentInstanceIdAndGarmentAreaIdAndDefectId(
                                    entity.getGarmentInstanceId(),
                                    garmentAreaId,
                                    defectId
                            );

            if (exists) {
                throw new BadRequestException(
                        "Garment expected defect already exists."
                );
            }

            entity.setGarmentAreaId(garmentAreaId);
            entity.setDefectId(defectId);
        }

        if (request.getSeverityLevelId() != null) {
            entity.setSeverityLevelId(
                    request.getSeverityLevelId()
            );
        }

        garmentExpectedDefectRepository.save(entity);

        return SingleResponse.success(
                null,
                "Garment expected defect successfully updated"
        );
    }

    @Override
    public SingleResponse<PageResponse<ListOfGarmentExpectedDefectResponse>>
    getAllGarmentExpectedDefects(Pageable pageable) {

        Page<GarmentExpectedDefect> page =
                garmentExpectedDefectRepository.findAll(pageable);

        List<ListOfGarmentExpectedDefectResponse> content =
                page.getContent()
                        .stream()
                        .map(entity ->
                                modelMapper.map(
                                        entity,
                                        ListOfGarmentExpectedDefectResponse.class
                                )
                        )
                        .toList();

        PageResponse<ListOfGarmentExpectedDefectResponse> pageResponse =
                new PageResponse<>();

        pageResponse.setContent(content);
   //   pageResponse.setPageNumber(page.getNumber());
     // pageResponse.setPageSize(page.getSize());
        pageResponse.setTotalElements(page.getTotalElements());
        pageResponse.setTotalPages(page.getTotalPages());

        return SingleResponse.success(pageResponse);
    }
}
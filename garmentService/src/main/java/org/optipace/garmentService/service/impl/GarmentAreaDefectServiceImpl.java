package org.optipace.garmentService.service.impl;

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
import org.optipace.garmentService.service.GarmentAreaDefectService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GarmentAreaDefectServiceImpl implements GarmentAreaDefectService {

    private final GarmentAreaDefectRepository garmentAreaDefectRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public SingleResponse<?> createGarmentAreaDefect(GarmentAreaDefectRequest request, String userId) {

        boolean exists = garmentAreaDefectRepository.existsByGarmentAreaIdAndDefectId(request.getGarmentAreaId(), request.getDefectId());

        if (exists) {
            throw new BadRequestException("Garment area defect already exists.");
        }

        GarmentAreaDefect entity = new GarmentAreaDefect();

        entity.setGarmentAreaId(request.getGarmentAreaId());
        entity.setDefectId(request.getDefectId());
        entity.setSeverityLevelId(request.getSeverityLevelId());

        entity.setMaxDeductionMarks(request.getMaxDeductionMarks() != null ? request.getMaxDeductionMarks() : BigDecimal.ONE);

        entity.setIsMandatory(request.getIsMandatory() != null ? request.getIsMandatory() : false);

        entity.setAiConfidenceThreshold(request.getAiConfidenceThreshold());

        entity.setRemarks(request.getRemarks());

        entity.setCreatedBy(Long.parseLong(userId));

        GarmentAreaDefect saved = garmentAreaDefectRepository.save(entity);

        GarmentAreaDefectResponse response = modelMapper.map(saved, GarmentAreaDefectResponse.class);

        return SingleResponse.success(response, "Garment area defect successfully created");
    }

    @Override
    public SingleResponse<GarmentAreaDefectResponse> getGarmentAreaDefectById(Long garmentAreaDefectId) {
        GarmentAreaDefect entity = garmentAreaDefectRepository.findById(garmentAreaDefectId).orElseThrow(() -> new NotFoundException("Garment area defect not found"));
        GarmentAreaDefectResponse response = modelMapper.map(entity, GarmentAreaDefectResponse.class);
        return SingleResponse.success(response);
    }

    @Override
    public SingleResponse<PageResponse<ListOfGarmentAreaDefectResponse>> getAllGarmentAreaDefects(Pageable pageable) {

        Page<GarmentAreaDefect> page = garmentAreaDefectRepository.findAll(pageable);

        List<ListOfGarmentAreaDefectResponse> content = page.getContent().stream()
                .map(entity -> modelMapper.map(entity, ListOfGarmentAreaDefectResponse.class)).toList();

        PageResponse<ListOfGarmentAreaDefectResponse> pageResponse = new PageResponse<>();
        pageResponse.setContent(content);
        pageResponse.setTotalElements(page.getTotalElements());
        pageResponse.setTotalPages(page.getTotalPages());
        return SingleResponse.success(pageResponse);
    }

    @Override
    @Transactional
    public SingleResponse<?> updateGarmentAreaDefect(Long garmentAreaDefectId, GarmentAreaDefectUpdateRequest request, String userId) {

        GarmentAreaDefect entity = garmentAreaDefectRepository.findById(garmentAreaDefectId).orElseThrow(() -> new NotFoundException("Garment area defect not found"));

        Long garmentAreaId = request.getGarmentAreaId() != null ? request.getGarmentAreaId() : entity.getGarmentAreaId();

        Long defectId = request.getDefectId() != null ? request.getDefectId() : entity.getDefectId();

        boolean combinationChanged = !garmentAreaId.equals(entity.getGarmentAreaId()) || !defectId.equals(entity.getDefectId());

        if (combinationChanged) {

            boolean exists = garmentAreaDefectRepository.existsByGarmentAreaIdAndDefectIdAndGarmentAreaDefectIdNot(garmentAreaId, defectId, garmentAreaDefectId);

            if (exists) {
                throw new BadRequestException("Garment area defect already exists.");
            }

            entity.setGarmentAreaId(garmentAreaId);
            entity.setDefectId(defectId);
        }

        if (request.getSeverityLevelId() != null) {
            entity.setSeverityLevelId(request.getSeverityLevelId());
        }

        if (request.getMaxDeductionMarks() != null) {
            entity.setMaxDeductionMarks(request.getMaxDeductionMarks());
        }

        if (request.getIsMandatory() != null) {
            entity.setIsMandatory(request.getIsMandatory());
        }

        if (request.getAiConfidenceThreshold() != null) {
            entity.setAiConfidenceThreshold(request.getAiConfidenceThreshold());
        }

        if (request.getRemarks() != null) {
            entity.setRemarks(request.getRemarks());
        }

        if (request.getRecordStatus() != null) {
            entity.getRecordStatus();
        }

        entity.setUpdatedBy(Long.parseLong(userId));

        garmentAreaDefectRepository.save(entity);

        return SingleResponse.success(null, "Garment area defect successfully updated");
    }

    @Override
    @Transactional
    public SingleResponse<?> deleteGarmentAreaDefect(Long garmentAreaDefectId, String userId) {

        GarmentAreaDefect garmentAreaDefect = garmentAreaDefectRepository.findById(garmentAreaDefectId).orElseThrow(() -> new NotFoundException("Garment area defect not found"));
        //garmentAreaDefect.setRecordStatus("D");
        garmentAreaDefect.setUpdatedBy(Long.parseLong(userId));
        return SingleResponse.success(null, "Garment area defect successfully deleted");
    }
}
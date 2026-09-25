package org.optipace.garmentService.service.impl;

import java.util.List;

import org.optipace.garmentService.dto.request.AddDefectMasterRequest;
import org.optipace.garmentService.dto.request.UpdateDefectMasterRequest;
import org.optipace.garmentService.dto.response.DefectMasterResponse;
import org.optipace.garmentService.dto.response.ListOfDefectMasterResponse;
import org.optipace.garmentService.dto.response.PageResponse;
import org.optipace.garmentService.dto.response.SingleResponse;
import org.optipace.garmentService.entity.DefectMaster;
import org.optipace.garmentService.enums.CustomStatus;
import org.optipace.garmentService.exception.BadRequestException;
import org.optipace.garmentService.exception.NotFoundException;
import org.optipace.garmentService.repository.DefectMasterRepository;
import org.optipace.garmentService.service.DefectMasterService;

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
public class DefectMasterServiceImpl implements DefectMasterService {

	private final DefectMasterRepository defectMasterRepository;

	@Override
	@Transactional
	public SingleResponse<?> createDefectMaster(AddDefectMasterRequest request, String adminId) {

		log.info("Initiating defect creation for code: {} by Admin: {}", request.getDefectCode(), adminId);

		if (request.getDefectCategoryId() == null) {
			throw new BadRequestException("Defect category ID is required");
		}

		String defectCode = null;

		if (request.getDefectCode() != null && !request.getDefectCode().trim().isEmpty()) {

			defectCode = request.getDefectCode().trim().toUpperCase();

			if (defectMasterRepository.existsByDefectCode(defectCode)) {

				throw new BadRequestException("Defect with code " + defectCode + " already exists");
			}
		}

		String defectName = null;

		if (request.getDefectName() != null && !request.getDefectName().trim().isEmpty()) {

			defectName = request.getDefectName().trim();
		}

		DefectMaster defectMaster = new DefectMaster();

		defectMaster.setDefectCategoryId(request.getDefectCategoryId());

		defectMaster.setDefectCode(defectCode);

		defectMaster.setDefectName(defectName);

		defectMaster.setDescription(request.getDescription());

		defectMaster.setDisplayOrder(request.getDisplayOrder());

		DefectMaster saved = defectMasterRepository.save(defectMaster);

		log.info("Defect successfully created with ID: {}", saved.getDefectId());

		return SingleResponse.success("Defect created successfully"
				+ (saved.getDefectCode() != null ? " with code: " + saved.getDefectCode() : ""));
	}

	@Override
	@Transactional(readOnly = true)
	public SingleResponse<PageResponse<ListOfDefectMasterResponse>> getAllDefectMasters(Pageable pageable) {

		Pageable sortedPageable = pageable.getSort().isSorted() ? pageable
				: PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
						Sort.by(Sort.Order.asc("defectName").nullsLast()));

		Page<DefectMaster> defectMasterPage = defectMasterRepository.findAll(sortedPageable);

		List<ListOfDefectMasterResponse> responseList = defectMasterPage.getContent().stream()
				.map(defect -> new ListOfDefectMasterResponse(defect.getDefectId(), defect.getDefectCategoryId(),
						defect.getDefectCode(), defect.getDefectName(), defect.getDescription(),
						defect.getDisplayOrder()))
				.toList();

		PageResponse<ListOfDefectMasterResponse> pageResponse = PageResponse
				.of(defectMasterPage.map(defectMaster -> new ListOfDefectMasterResponse(defectMaster.getDefectId(),
						defectMaster.getDefectCategoryId(), defectMaster.getDefectCode(), defectMaster.getDefectName(),
						defectMaster.getDescription(), defectMaster.getDisplayOrder())));

		return SingleResponse.success(pageResponse);
	}

	@Override
	@Transactional(readOnly = true)
	public SingleResponse<DefectMasterResponse> getDefectMasterById(Long defectId) {

		DefectMaster defectMaster = defectMasterRepository.findByDefectId(defectId)
				.orElseThrow(() -> new NotFoundException("Defect not found with ID: " + defectId));

		DefectMasterResponse response = mapToResponse(defectMaster);

		return SingleResponse.success(response);
	}

	@Override
	@Transactional
	public SingleResponse<?> updateDefectMaster(Long defectId, UpdateDefectMasterRequest request, String adminId) {

		log.info("Initiating defect update for ID: {} by Admin: {}", defectId, adminId);

		DefectMaster defectMaster = defectMasterRepository.findByDefectId(defectId)
				.orElseThrow(() -> new NotFoundException("Defect not found with ID: " + defectId));

		if (request.getDefectCategoryId() == null) {
			throw new BadRequestException("Defect category ID is required");
		}

		String defectCode = null;

		if (request.getDefectCode() != null && !request.getDefectCode().trim().isEmpty()) {

			defectCode = request.getDefectCode().trim().toUpperCase();

			if (defectMasterRepository.existsByDefectCodeAndDefectIdNot(defectCode, defectId)) {

				throw new BadRequestException("Defect with code " + defectCode + " already exists");
			}
		}

		String defectName = null;

		if (request.getDefectName() != null && !request.getDefectName().trim().isEmpty()) {

			defectName = request.getDefectName().trim();
		}

		defectMaster.setDefectCategoryId(request.getDefectCategoryId());

		defectMaster.setDefectCode(defectCode);

		defectMaster.setDefectName(defectName);

		defectMaster.setDescription(request.getDescription());

		defectMaster.setDisplayOrder(request.getDisplayOrder());

		defectMasterRepository.save(defectMaster);

		log.info("Defect successfully updated with ID: {}", defectId);

		return SingleResponse.success("Defect updated successfully");
	}

//    @Override
//    @Transactional
//    public SingleResponse<?> deleteDefectMasterById(
//            Long defectId,
//            String adminId) {
//
//        log.info(
//                "Initiating defect deletion for ID: {} by Admin: {}",
//                defectId,
//                adminId
//        );
//
//        DefectMaster defectMaster =
//                defectMasterRepository
//                        .findByDefectId(defectId)
//                        .orElseThrow(() ->
//                                new NotFoundException(
//                                        "Defect not found with ID: "
//                                                + defectId
//                                )
//                        );
//
//        defectMasterRepository.delete(defectMaster);
//
//        log.info(
//                "Defect successfully deleted with ID: {}",
//                defectId
//        );
//
//        return new SingleResponse<>(
//                "Defect deleted successfully",
//                CustomStatus.SUCCESS
//        );
//    }

	private DefectMasterResponse mapToResponse(DefectMaster defectMaster) {

		return new DefectMasterResponse(defectMaster.getDefectId(), defectMaster.getDefectCategoryId(),
				defectMaster.getDefectCode(), defectMaster.getDefectName(), defectMaster.getDescription(),
				defectMaster.getDisplayOrder());
	}
}
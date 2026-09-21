package org.optipace.garmentService.service.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.apache.http.HttpStatus;
import org.modelmapper.ModelMapper;
import org.optipace.garmentService.dto.request.GarmentLotRequest;
import org.optipace.garmentService.dto.request.GarmentLotUpdateRequest;
import org.optipace.garmentService.dto.response.GarmentLotResponse;
import org.optipace.garmentService.dto.response.ListOfGarmentLotResponse;
import org.optipace.garmentService.dto.response.PageResponse;
import org.optipace.garmentService.dto.response.SingleResponse;
import org.optipace.garmentService.entity.GarmentLot;
import org.optipace.garmentService.exception.BadRequestException;
import org.optipace.garmentService.exception.NotFoundException;
import org.optipace.garmentService.repository.GarmentLotRepository;
import org.optipace.garmentService.service.GarmentLotService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GarmentLotServiceImpl implements GarmentLotService {

    private final GarmentLotRepository garmentLotRepository;
    private final ModelMapper modelMapper;

    @Override
    public SingleResponse<PageResponse<ListOfGarmentLotResponse>> getAllGarmentLots(Pageable pageable) {

        Pageable sortedPageable = pageable.getSort().isSorted() ? pageable : PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Order.asc("lotNumber").nullsLast()));

        Page<GarmentLot> garmentLotPage = garmentLotRepository.findByRecordStatus("A", sortedPageable);

        Page<ListOfGarmentLotResponse> responsePage = garmentLotPage.map(garmentLot -> modelMapper.map(garmentLot, ListOfGarmentLotResponse.class));

        PageResponse<ListOfGarmentLotResponse> pageResponse = PageResponse.of(responsePage);

        return SingleResponse.success(pageResponse);
    }

    @Override
    public SingleResponse<?> createGarmentLot(GarmentLotRequest request, String userId) {
        if (garmentLotRepository.existsByLotNumber(request.getLotNumber())) {

            throw new BadRequestException("Garment lot number already exists.");
        }

        GarmentLot garmentLot = new GarmentLot();

        garmentLot.setLotNumber(request.getLotNumber());
        garmentLot.setGarmentTypeId(request.getGarmentTypeId());
        garmentLot.setFactoryId(request.getFactoryId());
        garmentLot.setProductionDate(request.getProductionDate());
        garmentLot.setQuantity(request.getQuantity());
        garmentLot.setBuyerName(request.getBuyerName());
        garmentLot.setPurchaseOrderNo(request.getPurchaseOrderNo());
        garmentLot.setColor(request.getColor());
        garmentLot.setSize(request.getSize());
        garmentLot.setRemarks(request.getRemarks());
        // Audit fields
        garmentLot.setCreatedBy(Long.parseLong(userId));
        garmentLot.setVersionNo(1);

        // Active record
        garmentLot.setRecordStatus("A");
        garmentLotRepository.save(garmentLot);
        return SingleResponse.success(HttpStatus.SC_CREATED, "Garment lot successfully created with lot number: " + garmentLot.getLotNumber());
    }


    @Override
    @Transactional
    public SingleResponse<?> updateGarmentLot(String lotNumber, GarmentLotUpdateRequest request, String userId) {

        GarmentLot garmentLot = garmentLotRepository.findByLotNumber(lotNumber)
                .orElseThrow(() -> new NotFoundException("Garment lot not found"));

        if (request.getLotNumber() != null && !request.getLotNumber().equals(garmentLot.getLotNumber())) {
            if (garmentLotRepository.existsByLotNumber(request.getLotNumber())) {
                throw new BadRequestException("Garment lot number already exists.");
            }
            garmentLot.setLotNumber(request.getLotNumber());
        }

        if (request.getGarmentTypeId() != null) garmentLot.setGarmentTypeId(request.getGarmentTypeId());
        if (request.getFactoryId() != null) garmentLot.setFactoryId(request.getFactoryId());
        if (request.getProductionDate() != null) garmentLot.setProductionDate(request.getProductionDate());
        if (request.getQuantity() != null) garmentLot.setQuantity(request.getQuantity());
        if (request.getBuyerName() != null) garmentLot.setBuyerName(request.getBuyerName());
        if (request.getPurchaseOrderNo() != null) garmentLot.setPurchaseOrderNo(request.getPurchaseOrderNo());
        if (request.getColor() != null) garmentLot.setColor(request.getColor());
        if (request.getSize() != null) garmentLot.setSize(request.getSize());
        if (request.getRemarks() != null) garmentLot.setRemarks(request.getRemarks());

        garmentLot.setUpdatedBy(Long.parseLong(userId));
        garmentLotRepository.save(garmentLot);

        return SingleResponse.success(null, "Garment lot successfully updated");
    }

    @Override
    public SingleResponse<GarmentLotResponse> getGarmentLotByLotNumber(String lotNumber) {
        GarmentLot garmentLot = garmentLotRepository.findByLotNumber(lotNumber).orElseThrow(() -> new NotFoundException("Garment lot not found"));
        GarmentLotResponse response = modelMapper.map(garmentLot, GarmentLotResponse.class);
        return SingleResponse.success(response);
    }

    @Override
    @Transactional
    public SingleResponse<?> deleteGarmentLotByLotNumber(String lotNumber, String userId) {
        GarmentLot garmentLot = garmentLotRepository.findByLotNumber(lotNumber).orElseThrow(() -> new NotFoundException("Garment lot not found"));
        garmentLotRepository.delete(garmentLot);
        return SingleResponse.success(null, "Garment lot successfully deleted");
    }

}
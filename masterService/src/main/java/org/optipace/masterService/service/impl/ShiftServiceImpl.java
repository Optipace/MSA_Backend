package org.optipace.masterService.service.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.optipace.masterService.dto.request.AddShiftRequest;
import org.optipace.masterService.dto.request.UpdateShiftRequest;
import org.optipace.masterService.dto.response.ListOfShiftResponse;
import org.optipace.masterService.dto.response.PageResponse;
import org.optipace.masterService.dto.response.ShiftResponse;
import org.optipace.masterService.dto.response.SingleResponse;
import org.optipace.masterService.entity.Shift;
import org.optipace.masterService.enums.CustomStatus;
import org.optipace.masterService.exception.BadRequestException;
import org.optipace.masterService.exception.NotFoundException;
import org.optipace.masterService.repository.FactoryRepository;
import org.optipace.masterService.repository.ShiftRepository;
import org.optipace.masterService.service.ShiftService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ShiftServiceImpl implements ShiftService {

    private final ShiftRepository shiftRepository;
    private final FactoryRepository factoryRepository;
    private final ModelMapper modelMapper;

    @Override
    public SingleResponse<?> createShift(AddShiftRequest request,String adminId) { 	
        log.info( "Initiating shift creation for code: {} by Admin: {}",request.getShiftCode(),adminId);
        if (shiftRepository.existsByFactory_FactoryIdAndShiftCodeAndRecordStatus(
                request.getFactoryId(),
                request.getShiftCode(),
                'A')) {            
        	log.warn("Shift code {} already exists in factory {}",request.getShiftCode(),request.getFactoryId());
            throw new BadRequestException("Shift code already exists in this factory");
            }

        factoryRepository.findById(request.getFactoryId())
                .orElseThrow(() ->
                        new NotFoundException("Factory not found with ID: "+ request.getFactoryId()));

        Shift shift = new Shift();
        shift.setFactory(factoryRepository.getReferenceById(request.getFactoryId()));

        shift.setShiftCode(request.getShiftCode());
        shift.setShiftName(request.getShiftName());
        shift.setStartTime(request.getStartTime());
        shift.setEndTime(request.getEndTime());
        shift.setBreakDurationMinutes(request.getBreakDurationMinutes());
        shift.setRemarks(request.getRemarks());
        shift.setCreatedBy(Long.parseLong(adminId));
        shift.setVersionNo(1);
        shift.setRecordStatus('A');

        shiftRepository.save(shift);
        log.info("Shift successfully created with ID: {}",shift.getShiftId());
        return new SingleResponse<>(
        		"Shift successfully created with code: "+ shift.getShiftCode(),
        		CustomStatus.SUCCESS
        		);
        }

    @Override
    public SingleResponse<PageResponse<ListOfShiftResponse>> getAllShift(Pageable pageable) {
        Pageable sortedPageable =pageable.getSort().isSorted()? pageable : 
        	PageRequest.of(pageable.getPageNumber(),pageable.getPageSize(),
        			Sort.by(Sort.Order.asc("shiftName").nullsLast()));
        
        Page<Shift> shiftPage = shiftRepository.findByRecordStatus('A', sortedPageable);
        List<ListOfShiftResponse> shiftResponseList = shiftPage.getContent().stream()
        		.map(shift -> 
        		modelMapper.map(shift,ListOfShiftResponse.class))
                .toList();

        PageResponse<ListOfShiftResponse> pageResponse = new PageResponse<>(
                        shiftResponseList,
                        shiftPage.getNumber(),
                        shiftPage.getSize(),
                        shiftPage.getTotalElements(),
                        shiftPage.getTotalPages(),
                        shiftPage.isLast()
                        );
        return new SingleResponse<>(pageResponse,CustomStatus.SUCCESS);
        }

    @Override
    @Transactional
    public SingleResponse<?> updateShift(Long shiftId, UpdateShiftRequest request, String adminId) {
        log.info("Initiating shift update for ID: {} by Admin: {}", shiftId, adminId);

        Shift shift = shiftRepository.findByShiftIdAndRecordStatus(shiftId, 'A')
                .orElseThrow(() ->
                        new NotFoundException("Active shift not found with ID: " + shiftId));
        
        Long factoryId = shift.getFactory().getFactoryId();
        if (request.getFactoryId() != null) {
        	factoryId = request.getFactoryId();
            }

        String shiftCode = shift.getShiftCode();
        if (request.getShiftCode() != null && !request.getShiftCode().trim().isEmpty()) {
            shiftCode = request.getShiftCode();
            }

        if (request.getShiftCode() != null || request.getFactoryId() != null) {

            if (shiftRepository
                    .existsByFactory_FactoryIdAndShiftCodeAndShiftIdNotAndRecordStatus(
                            factoryId,
                            shiftCode,
                            shiftId,
                            'A')) {

                throw new BadRequestException("Shift code already exists in this factory");            
                }
        }

        if (request.getFactoryId() != null && !request.getFactoryId()
                .equals(shift.getFactory().getFactoryId())) {
            factoryRepository.findById(request.getFactoryId())
                    .orElseThrow(() -> new NotFoundException("Factory not found with ID: " + request.getFactoryId()));
            shift.setFactory(factoryRepository.getReferenceById(request.getFactoryId()));
            }

        if (request.getShiftCode() != null && !request.getShiftCode()
                .trim()
                .isEmpty()) {
            shift.setShiftCode(request.getShiftCode());
            }

        if (request.getShiftName() != null && !request.getShiftName()
                .trim()
                .isEmpty()) {
            shift.setShiftName(request.getShiftName());
            }

        if (request.getStartTime() != null) {
            shift.setStartTime(request.getStartTime());
            }

        if (request.getEndTime() != null) {
            shift.setEndTime(request.getEndTime());
            }

        if (request.getBreakDurationMinutes() != null) {
            shift.setBreakDurationMinutes(request.getBreakDurationMinutes());
            }

        if (request.getRemarks() != null) {
            shift.setRemarks(request.getRemarks());
            }

        shift.setUpdatedBy(Long.parseLong(adminId));
        shiftRepository.save(shift);
        log.info("Successfully updated shift ID: {}", shiftId);
        return new SingleResponse<>("Shift updated successfully", CustomStatus.SUCCESS);
        }

    @Override
    public SingleResponse<ShiftResponse> getShiftById(Long shiftId) {
        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> 
                new NotFoundException("Shift not found with ID: " + shiftId));
        ShiftResponse response = modelMapper.map(shift, ShiftResponse.class);
        if (shift.getFactory() != null) {
            response.setFactoryId(shift.getFactory().getFactoryId());
            }
        return new SingleResponse<>(response, CustomStatus.SUCCESS);
        }

    @Override
    public SingleResponse<?> deleteShiftById(Long shiftId, String adminId) {
        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() ->
                        new NotFoundException("Shift not found"));

        shift.setRecordStatus('D');
        shift.setUpdatedBy(Long.parseLong(adminId));
        shiftRepository.save(shift);
        return new SingleResponse<>("Shift successfully deleted", CustomStatus.SUCCESS);
        }
    }

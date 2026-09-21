package org.optipace.masterService.service.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.optipace.masterService.dto.request.AddCityRequest;
import org.optipace.masterService.dto.request.UpdateCityRequest;
import org.optipace.masterService.dto.response.CityResponse;
import org.optipace.masterService.dto.response.ListOfCityResponse;
import org.optipace.masterService.dto.response.PageResponse;
import org.optipace.masterService.dto.response.SingleResponse;
import org.optipace.masterService.entity.City;
import org.optipace.masterService.enums.CustomStatus;
import org.optipace.masterService.exception.BadRequestException;
import org.optipace.masterService.exception.NotFoundException;
import org.optipace.masterService.repository.CityRepository;
import org.optipace.masterService.repository.StateRepository;
import org.optipace.masterService.service.CityService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class CityServiceImpl implements CityService {

    private final CityRepository cityRepository;
    private final StateRepository stateRepository;
    private final ModelMapper modelMapper;

    @Override
    public SingleResponse<?> createCity(AddCityRequest request, String adminId) {
        log.info("Initiating city creation for code: {} by Admin: {}", request.getCityCode(), adminId);      
         // Check duplicate only among ACTIVE cities.                 
        if (cityRepository.existsByState_StateIdAndCityCodeAndCityName(request.getStateId(), request.getCityCode(), request.getCityName())) {
            log.warn("City {} already exists in state {}", request.getCityName(), request.getCityCode(), request.getStateId());
            throw new BadRequestException("City already exists in this state");
            }        
         // Validate State exists        
        stateRepository.findById(request.getStateId())
                .orElseThrow(() ->
                        new NotFoundException("State not found with ID: " + request.getStateId()));

        City city = new City();
        city.setState(stateRepository.getReferenceById(request.getStateId()));
        city.setCityCode(request.getCityCode());
        city.setCityName(request.getCityName());
        city.setLatitude(request.getLatitude());
        city.setLongitude(request.getLongitude());
        city.setRemarks(request.getRemarks());
        city.setCreatedBy(Long.parseLong(adminId));
        city.setVersionNo(1);
        city.setRecordStatus('A');

        cityRepository.save(city);

        log.info("City successfully created with ID: {}",city.getCityId());
        return new SingleResponse<>("City successfully created with code: " + city.getCityCode(),CustomStatus.SUCCESS);
    }

    @Override
    public SingleResponse<PageResponse<ListOfCityResponse>> getAllCity(Pageable pageable) {
        Pageable sortedPageable = pageable.getSort().isSorted()? pageable
                        : PageRequest.of(pageable.getPageNumber(),pageable.getPageSize(),
                                Sort.by(Sort.Order.asc("cityName").nullsLast()));

        Page<City> cityPage = cityRepository.findByRecordStatus('A', sortedPageable);

        List<ListOfCityResponse> cityResponseList = cityPage.getContent()
                        .stream()
                        .map(city -> modelMapper.map(city, ListOfCityResponse.class))
                        .toList();
       
        cityPage.getContent().forEach(city -> {
                    ListOfCityResponse response = cityResponseList.get(cityPage.getContent().indexOf(city));
                    if (city.getState() != null) {
                        response.setStateId(city.getState().getStateId());
                    }
                });

        PageResponse<ListOfCityResponse> pageResponse = new PageResponse<>(
                        cityResponseList,
                        cityPage.getNumber(),
                        cityPage.getSize(),
                        cityPage.getTotalElements(),
                        cityPage.getTotalPages(),
                        cityPage.isLast()
                );
        return new SingleResponse<>(pageResponse, CustomStatus.SUCCESS);
    }

    @Override
    @Transactional
    public SingleResponse<?> updateCity(Long cityId, UpdateCityRequest request, String adminId) {
        log.info("Initiating city update for ID: {} by Admin: {}", cityId, adminId);       
         // Only ACTIVE city can be updated.
        City city = cityRepository.findByCityIdAndRecordStatus(cityId, 'A').orElseThrow(() ->
                        new NotFoundException("Active city not found with ID: " + cityId));
         // Existing values        
        Long stateId = city.getState().getStateId();
        if (request.getStateId() != null) {stateId = request.getStateId();
        }
        String cityName = city.getCityName();
        if (request.getCityName() != null && !request.getCityName().trim().isEmpty()) {
            cityName = request.getCityName();
        }      
         // Duplicate validation.         
        if (request.getStateId() != null || request.getCityName() != null) {
            if (cityRepository.existsByState_StateIdAndCityNameAndCityIdNotAndRecordStatus(stateId, cityName, cityId, 'A')) {
                throw new BadRequestException("City already exists in this state");
            }
        }        
        // Validate and update State only when changed.         
        if (request.getStateId() != null && !request.getStateId()
                .equals(city.getState().getStateId())) {
            stateRepository.findById(request.getStateId())
                    .orElseThrow(() ->
                            new NotFoundException("State not found with ID: " + request.getStateId()));

            city.setState(stateRepository.getReferenceById(request.getStateId()));
        }

        // Update        
        if (request.getCityCode() != null && !request.getCityCode().trim().isEmpty()) {
            city.setCityCode(request.getCityCode());
        }
        
        if (request.getCityName() != null && !request.getCityName().trim().isEmpty()) {
            city.setCityName(request.getCityName());
        }

        if (request.getLatitude() != null) {
            city.setLatitude(request.getLatitude());
        }
        
        if (request.getLongitude() != null) {
            city.setLongitude(request.getLongitude());
        }

        if (request.getRemarks() != null) {
            city.setRemarks(request.getRemarks());
        }
        
        city.setUpdatedBy(Long.parseLong(adminId));
        cityRepository.save(city);
        log.info("Successfully updated city ID: {}", cityId);
        return new SingleResponse<>(
                "City updated successfully",
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<CityResponse> getCityById(Long cityId) {
        City city = cityRepository.findByCityIdAndRecordStatus(cityId, 'A')
                        .orElseThrow(() ->
                                new NotFoundException("Active City not found with ID: " + cityId));

        CityResponse response = modelMapper.map(city, CityResponse.class);

        if (city.getState() != null) {
            response.setStateId(city.getState().getStateId());
        }

        return new SingleResponse<>(
                response,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<?> deleteCityById(
            Long cityId,
            String adminId) {

        City city =
                cityRepository.findById(cityId)
                        .orElseThrow(() ->
                                new NotFoundException(
                                        "City not found"
                                ));

        /*
         * Soft delete
         */
        city.setRecordStatus('D');

        city.setUpdatedBy(
                Long.parseLong(adminId)
        );

        cityRepository.save(city);

        log.info(
                "City successfully deleted with ID: {}",
                cityId
        );

        return new SingleResponse<>(
                "City successfully deleted",
                CustomStatus.SUCCESS
        );
    }
}



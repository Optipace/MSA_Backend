package org.optipace.masterService.service.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.optipace.masterService.dto.request.FactoryRequest;
import org.optipace.masterService.dto.request.FactoryUpdateRequest;
import org.optipace.masterService.dto.response.*;
import org.optipace.masterService.entity.Factory;
import org.optipace.masterService.entity.Organization;
import org.optipace.masterService.enums.CustomStatus;
import org.optipace.masterService.exception.BadRequestException;
import org.optipace.masterService.exception.NotFoundException;
import org.optipace.masterService.repository.*;
import org.optipace.masterService.service.FactoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class FactoryServiceImpl implements FactoryService {

    private final FactoryRepository factoryRepository;
    private final ModelMapper modelMapper;
    private final LocationValidationHelper locationValidationHelper;
    private final OrganizationRepository organizationRepository;
    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;
    private final StateRepository stateRepository;

    @Override
    public SingleResponse<PageResponse<ListOfFactoriesResponse>> getAllFactories(Pageable pageable) {
        Pageable sortedPageable = pageable.getSort().isSorted() ? pageable :
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                        Sort.by(Sort.Order.asc("factoryName").nullsLast()));

        Page<Factory> activeFactoryPage = factoryRepository.findByRecordStatus('A',sortedPageable);
        List<Factory> factoryList = activeFactoryPage.getContent();

        List<ListOfFactoriesResponse> listOfFactoriesResponseList = factoryList.stream()
                .map((factory) -> {
                    ListOfFactoriesResponse factoriesResponse = modelMapper.map(factory, ListOfFactoriesResponse.class);
                    OrganizationResponse organizationResponse = modelMapper.map(factory.getOrganization(), OrganizationResponse.class);
                    factoriesResponse.setOrganizationResponse(organizationResponse);
                    return factoriesResponse;
                })
                .toList();

        PageResponse<ListOfFactoriesResponse> pageResponse = new PageResponse<>(
                listOfFactoriesResponseList,
                activeFactoryPage.getNumber(),
                activeFactoryPage.getSize(),
                activeFactoryPage.getTotalElements(),
                activeFactoryPage.getTotalPages(),
                activeFactoryPage.isLast()
        );
        return new SingleResponse<>(
                pageResponse,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<?> createFactory(FactoryRequest request, String factoryAdminId) {
        locationValidationHelper.verifyLocationContext(
                request.getCountryId(),
                request.getStateId(),
                request.getCityId()
        );

        if(factoryRepository.existsByFactoryCode(request.getFactoryCode())){
            throw new BadRequestException("Factory code already exists");
        }

        Organization organization = organizationRepository.findById(request.getOrganizationId())
                .orElseThrow(() -> new NotFoundException("Organization not found"));

        Factory factory = new Factory();
        factory.setFactoryCode(request.getFactoryCode());
        factory.setOrganization(organization);
        factory.setFactoryName(request.getFactoryName());
        factory.setShortName(request.getShortName());
        factory.setAddressLine1(request.getAddressLine1());
        factory.setAddressLine2(request.getAddressLine2());
        factory.setPostalCode(request.getPostalCode());
        factory.setLatitude(request.getLatitude());
        factory.setLongitude(request.getLongitude());
        factory.setContactPerson(request.getContactPerson());
        factory.setContactNumber(request.getContactNumber());
        factory.setEmail(request.getEmail());
        factory.setCapacityPerDay(request.getCapacityPerDay());
        factory.setEstablishedOn(request.getEstablishedOn());
        factory.setCreatedBy(Long.parseLong(factoryAdminId));
        factory.setRemarks(request.getRemarks());

        factory.setCountry(countryRepository.getReferenceById(request.getCountryId()));
        factory.setState(stateRepository.getReferenceById(request.getStateId()));
        factory.setCity(cityRepository.getReferenceById(request.getCityId()));

        factory.setRecordStatus('A'); // 'A' for Active record

        factoryRepository.save(factory);
        return new SingleResponse<>(
                "Factory successfully created with code: "+factory.getFactoryCode(),
                CustomStatus.SUCCESS
        );
    }

    @Transactional
    public SingleResponse<?> updateFactory(Long factoryId, FactoryUpdateRequest request, String factoryAdminId) {

        Factory factory = factoryRepository.findById(factoryId)
                .orElseThrow(() -> new NotFoundException("Factory not found"));

        if (request.getFactoryCode() != null || request.getOrganizationId() != null) {
            String codeToCheck = request.getFactoryCode() != null ? request.getFactoryCode() : factory.getFactoryCode();
            Long orgIdToCheck = request.getOrganizationId() != null ? request.getOrganizationId() : factory.getOrganization().getOrganizationId();

            boolean isDuplicate = factoryRepository.existsByFactoryCodeAndOrganization_OrganizationIdAndFactoryIdNot(
                    codeToCheck, orgIdToCheck, factoryId
            );
            if (isDuplicate) {
                throw new BadRequestException("Factory code already exists for this organization");
            }

            if (request.getFactoryCode() != null) factory.setFactoryCode(request.getFactoryCode());
            if (request.getOrganizationId() != null) factory.setOrganization(organizationRepository.getReferenceById(request.getOrganizationId()));
        }

        if (request.getFactoryName() != null) factory.setFactoryName(request.getFactoryName());
        if (request.getShortName() != null) factory.setShortName(request.getShortName());
        if (request.getAddressLine1() != null) factory.setAddressLine1(request.getAddressLine1());
        if (request.getAddressLine2() != null) factory.setAddressLine2(request.getAddressLine2());
        if (request.getPostalCode() != null) factory.setPostalCode(request.getPostalCode());
        if (request.getLatitude() != null) factory.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) factory.setLongitude(request.getLongitude());
        if (request.getContactPerson() != null) factory.setContactPerson(request.getContactPerson());
        if (request.getContactNumber() != null) factory.setContactNumber(request.getContactNumber());
        if (request.getEmail() != null) factory.setEmail(request.getEmail());
        if (request.getCapacityPerDay() != null) factory.setCapacityPerDay(request.getCapacityPerDay());
        if (request.getEstablishedOn() != null) factory.setEstablishedOn(request.getEstablishedOn());
        if (request.getRemarks() != null) factory.setRemarks(request.getRemarks());

        if (request.getCountryId() != null || request.getStateId() != null || request.getCityId() != null) {

            Long updatedCountryId = request.getCountryId() != null ? request.getCountryId() : factory.getCountry().getCountryId();
            Long updatedStateId = request.getStateId() != null ? request.getStateId() : factory.getState().getStateId();
            Long updatedCityId = request.getCityId() != null ? request.getCityId() : factory.getCity().getCityId();

            locationValidationHelper.verifyLocationContext(updatedCountryId, updatedStateId, updatedCityId);

            if (request.getCountryId() != null) factory.setCountry(countryRepository.getReferenceById(request.getCountryId()));
            if (request.getStateId() != null) factory.setState(stateRepository.getReferenceById(request.getStateId()));
            if (request.getCityId() != null) factory.setCity(cityRepository.getReferenceById(request.getCityId()));
        }

        factory.setUpdatedBy(Long.parseLong(factoryAdminId));

        factoryRepository.save(factory);

        Response meta = new Response();
        meta.setCode(200);
        meta.setMessage("Factory updated successfully");

        return new SingleResponse<>("Factory successfully updated with code: " + factory.getFactoryCode(), meta);
    }

    @Override
    public SingleResponse<FactoryResponse> getFactoryById(Long factoryId) {
        Factory factory = factoryRepository.findById(factoryId)
                .orElseThrow(() -> new NotFoundException("Factory not found"));

        FactoryResponse factoryResponse = modelMapper.map(factory, FactoryResponse.class);
        Organization organization = organizationRepository.findById(factory.getOrganization().getOrganizationId())
                .orElseThrow(() -> new NotFoundException("Organization not found for this factory"));

        OrganizationResponse organizationResponse = modelMapper.map(organization, OrganizationResponse.class);
        factoryResponse.setOrganizationResponse(organizationResponse);
        return new SingleResponse<>(
                factoryResponse,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<?> deleteFactoryById(Long factoryId, String superAdminId) {
        Factory factory = factoryRepository.findById(factoryId)
                .orElseThrow(() -> new NotFoundException("Factory not found"));

        factory.setRecordStatus('D');
        factory.setUpdatedBy(Long.parseLong(superAdminId));
        factoryRepository.save(factory);
        return new SingleResponse<>(
                "Factory successfully deleted",
                CustomStatus.SUCCESS
        );
    }
}

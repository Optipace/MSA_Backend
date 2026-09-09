package org.optipace.masterService.service.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.optipace.masterService.dto.request.OrganizationRequest;
import org.optipace.masterService.dto.request.OrganizationUpdateRequest;
import org.optipace.masterService.dto.response.ListOfOrganizationResponse;
import org.optipace.masterService.dto.response.OrganizationResponse;
import org.optipace.masterService.dto.response.PageResponse;
import org.optipace.masterService.dto.response.SingleResponse;
import org.optipace.masterService.entity.Organization;
import org.optipace.masterService.enums.CustomStatus;
import org.optipace.masterService.exception.BadRequestException;
import org.optipace.masterService.exception.NotFoundException;
import org.optipace.masterService.repository.CityRepository;
import org.optipace.masterService.repository.CountryRepository;
import org.optipace.masterService.repository.OrganizationRepository;
import org.optipace.masterService.repository.StateRepository;
import org.optipace.masterService.service.OrganizationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final LocationValidationHelper locationValidationHelper;
    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;
    private final StateRepository stateRepository;
    private final ModelMapper modelMapper;

    @Override
    public SingleResponse<PageResponse<ListOfOrganizationResponse>> getAllOrganization(Pageable pageable) {
        Pageable sortedPageable = pageable.getSort().isSorted() ? pageable :
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                        Sort.by(Sort.Order.asc("organizationName").nullsLast()));

        Page<Organization> activeOrganizationPage = organizationRepository.findByRecordStatus('A',sortedPageable);
        List<Organization> organizationList = activeOrganizationPage.getContent();

        List<ListOfOrganizationResponse> listOfOrganizationResponseList = organizationList.stream()
                .map((organization) -> modelMapper.map(organization, ListOfOrganizationResponse.class))
                .toList();

        PageResponse<ListOfOrganizationResponse> pageResponse = new PageResponse<>(
                listOfOrganizationResponseList,
                activeOrganizationPage.getNumber(),
                activeOrganizationPage.getSize(),
                activeOrganizationPage.getTotalElements(),
                activeOrganizationPage.getTotalPages(),
                activeOrganizationPage.isLast()
        );
        return new SingleResponse<>(
                pageResponse,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<?> createOrganization(OrganizationRequest request, String superAdminId) {
        locationValidationHelper.verifyLocationContext(
                request.getCountryId(),
                request.getStateId(),
                request.getCityId()
        );

        if (organizationRepository.existsByOrganizationCode(request.getCode())) {
            throw new BadRequestException("Organization code already exists.");
        }
        if (organizationRepository.existsByGstin(request.getGstin())) {
            throw new BadRequestException("GSTIN already exists.");
        }

        Organization organization = new Organization();

        organization.setOrganizationCode(request.getCode());
        organization.setOrganizationName(request.getName());
        organization.setLegalName(request.getLegalName());
        organization.setGstin(request.getGstin());
        organization.setPanNumber(request.getPanNumber());
        organization.setRegistrationNumber(request.getRegistrationNumber());
        organization.setAddressLine1(request.getAddressLine1());
        organization.setAddressLine2(request.getAddressLine2());
        organization.setPostalCode(request.getPostalCode());
        organization.setEmail(request.getEmail());
        organization.setPhoneNumber(request.getPhoneNumber());
        organization.setWebsite(request.getWebsite());
        organization.setCreatedBy(Long.parseLong(superAdminId));
        organization.setRemarks(request.getRemarks());

        organization.setCountry(countryRepository.getReferenceById(request.getCountryId()));
        organization.setState(stateRepository.getReferenceById(request.getStateId()));
        organization.setCity(cityRepository.getReferenceById(request.getCityId()));

        organization.setVersionNo(1);
        organization.setRecordStatus('A'); // 'A' for Active record

        organizationRepository.save(organization);

        return new SingleResponse<>(
                "Organization successfully created with code: " + organization.getOrganizationCode(),
                CustomStatus.SUCCESS
        );
    }

    @Transactional
    public SingleResponse<?> updateOrganization(Long organizationId, OrganizationUpdateRequest request, String superAdminId) {

        Organization org = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new NotFoundException("Organization not found"));

        if (request.getCode() != null) org.setOrganizationCode(request.getCode());

        if (request.getName() != null) org.setOrganizationName(request.getName());

        if (request.getLegalName() != null) org.setLegalName(request.getLegalName());

        if (request.getGstin() != null) org.setGstin(request.getGstin());

        if (request.getPanNumber() != null) org.setPanNumber(request.getPanNumber());

        if (request.getRegistrationNumber() != null) org.setRegistrationNumber(request.getRegistrationNumber());

        if (request.getAddressLine1() != null) org.setAddressLine1(request.getAddressLine1());

        if (request.getAddressLine2() != null) org.setAddressLine2(request.getAddressLine2());

        if (request.getPostalCode() != null) org.setPostalCode(request.getPostalCode());

        if (request.getEmail() != null) org.setEmail(request.getEmail());

        if (request.getPhoneNumber() != null) org.setPhoneNumber(request.getPhoneNumber());

        if (request.getWebsite() != null) org.setWebsite(request.getWebsite());

        if (request.getRemarks() != null) org.setRemarks(request.getRemarks());

        if (request.getCountryId() != null || request.getStateId() != null || request.getCityId() != null) {

            Long updatedCountryId = request.getCountryId() != null ? request.getCountryId() : org.getCountry().getCountryId();
            Long updatedStateId = request.getStateId() != null ? request.getStateId() : org.getState().getStateId();
            Long updatedCityId = request.getCityId() != null ? request.getCityId() : org.getCity().getCityId();

            locationValidationHelper.verifyLocationContext(updatedCountryId, updatedStateId, updatedCityId);

            if (request.getCountryId() != null) org.setCountry(countryRepository.getReferenceById(request.getCountryId()));
            if (request.getStateId() != null) org.setState(stateRepository.getReferenceById(request.getStateId()));
            if (request.getCityId() != null) org.setCity(cityRepository.getReferenceById(request.getCityId()));
        }

        org.setUpdatedBy(Long.parseLong(superAdminId));
        organizationRepository.save(org);

        return new SingleResponse<>(
                "Organization successfully updated",
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<OrganizationResponse> getOrganizationById(Long organizationId) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new NotFoundException("Organization not found"));

        OrganizationResponse organizationResponse = modelMapper.map(organization, OrganizationResponse.class);

        return new SingleResponse<>(
                organizationResponse,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<?> deleteOrganizationById(Long organizationId, String superAdminId) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new NotFoundException("Organization not found"));

        organization.setRecordStatus('D');
        organization.setUpdatedBy(Long.parseLong(superAdminId));
        organizationRepository.save(organization);
        return new SingleResponse<>(
                "Organization successfully deleted",
                CustomStatus.SUCCESS
        );
    }
}

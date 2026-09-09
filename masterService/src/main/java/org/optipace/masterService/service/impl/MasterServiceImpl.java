package org.optipace.masterService.service.impl;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.optipace.masterService.dto.response.*;
import org.optipace.masterService.entity.*;
import org.optipace.masterService.enums.CustomStatus;
import org.optipace.masterService.repository.*;
import org.optipace.masterService.service.MasterService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MasterServiceImpl implements MasterService {

    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;
    private final DepartmentRepository departmentRepository;
    private final DesignationRepository designationRepository;
    private final FactoryRepository factoryRepository;
    private final OrganizationRepository organizationRepository;
    private final SectionRepository sectionRepository;
    private final ShiftRepository shiftRepository;
    private final StateRepository stateRepository;
    private final ModelMapper modelMapper;

    @Override
    public SingleResponse<PageResponse<MasterDepartmentResponse>> getAllDepartments(Pageable pageable) {
        Pageable sortedPageable = pageable.getSort().isSorted() ? pageable :
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                        Sort.by(Sort.Order.asc("departmentName").nullsLast()));

        Page<Department> departmentPage = departmentRepository.findByRecordStatus('A', sortedPageable);
        List<Department> departmentList = departmentPage.getContent();

        List<MasterDepartmentResponse> DepartmentResponseList = departmentList.stream()
                .map(department -> modelMapper.map(department, MasterDepartmentResponse.class))
                .toList();

        PageResponse<MasterDepartmentResponse> pageResponse = new PageResponse<>(
                DepartmentResponseList,
                departmentPage.getNumber(),
                departmentPage.getSize(),
                departmentPage.getTotalElements(),
                departmentPage.getTotalPages(),
                departmentPage.isLast()
        );

        return new SingleResponse<>(
                pageResponse,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<PageResponse<MasterDesignationResponse>> getAllDesignations(Pageable pageable) {
        Page<Designation> designationPage = designationRepository.findByRecordStatusOrderByHierarchyLevelAsc('A', pageable);
        List<Designation> designationList = designationPage.getContent();

        List<MasterDesignationResponse> designationResponseList = designationList.stream()
                .map(designation -> modelMapper.map(designation, MasterDesignationResponse.class))
                .toList();

        PageResponse<MasterDesignationResponse> pageResponse = new PageResponse<>(
                designationResponseList,
                designationPage.getNumber(),
                designationPage.getSize(),
                designationPage.getTotalElements(),
                designationPage.getTotalPages(),
                designationPage.isLast()
        );

        return new SingleResponse<>(
                pageResponse,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<PageResponse<MasterOrganizationResponse>> getAllOrganizations(Pageable pageable) {
        Pageable sortedPageable = pageable.getSort().isSorted() ? pageable :
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                        Sort.by(Sort.Order.asc("organizationName").nullsLast()));

        Page<Organization> activeOrganizationPage = organizationRepository.findByRecordStatus('A',sortedPageable);
        List<Organization> organizationList = activeOrganizationPage.getContent();

        List<MasterOrganizationResponse> organizationResponseList = organizationList.stream()
                .map((organization) -> modelMapper.map(organization, MasterOrganizationResponse.class))
                .toList();

        PageResponse<MasterOrganizationResponse> pageResponse = new PageResponse<>(
                organizationResponseList,
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
    public SingleResponse<PageResponse<MasterSectionResponse>> getAllSections(Pageable pageable) {
//        Pageable sortedPageable = pageable.getSort().isSorted() ? pageable :
//                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
//                        Sort.by(Sort.Order.asc("sectionName").nullsLast()));

        Page<Section> sectionPage = sectionRepository.findByRecordStatus('A',pageable);
        List<Section> sectionList = sectionPage.getContent();

        List<MasterSectionResponse> sectionResponseList = sectionList.stream()
                .map((section) -> modelMapper.map(section, MasterSectionResponse.class))
                .toList();

        PageResponse<MasterSectionResponse> pageResponse = new PageResponse<>(
                sectionResponseList,
                sectionPage.getNumber(),
                sectionPage.getSize(),
                sectionPage.getTotalElements(),
                sectionPage.getTotalPages(),
                sectionPage.isLast()
        );
        return new SingleResponse<>(
                pageResponse,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<PageResponse<MasterShiftResponse>> getAllShifts(Pageable pageable) {
        Page<Shift> shiftPage = shiftRepository.findByRecordStatus('A',pageable);
        List<Shift> shiftList = shiftPage.getContent();

        List<MasterShiftResponse> sectionResponseList = shiftList.stream()
                .map((shift) -> modelMapper.map(shift, MasterShiftResponse.class))
                .toList();

        PageResponse<MasterShiftResponse> pageResponse = new PageResponse<>(
                sectionResponseList,
                shiftPage.getNumber(),
                shiftPage.getSize(),
                shiftPage.getTotalElements(),
                shiftPage.getTotalPages(),
                shiftPage.isLast()
        );
        return new SingleResponse<>(
                pageResponse,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<PageResponse<MasterCountryResponse>> getAllCountries(Pageable pageable) {
        Pageable sortedPageable = pageable.getSort().isSorted() ? pageable :
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                        Sort.by(Sort.Order.asc("countryName").nullsLast()));

        Page<Country> countryPage = countryRepository.findByRecordStatus('A',sortedPageable);
        List<Country> countryList = countryPage.getContent();

        List<MasterCountryResponse> countryResponseList = countryList.stream()
                .map((country) -> modelMapper.map(country, MasterCountryResponse.class))
                .toList();

        PageResponse<MasterCountryResponse> pageResponse = new PageResponse<>(
                countryResponseList,
                countryPage.getNumber(),
                countryPage.getSize(),
                countryPage.getTotalElements(),
                countryPage.getTotalPages(),
                countryPage.isLast()
        );
        return new SingleResponse<>(
                pageResponse,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<PageResponse<MasterStateResponse>> getAllStates(Pageable pageable) {
        Pageable sortedPageable = pageable.getSort().isSorted() ? pageable :
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                        Sort.by(Sort.Order.asc("stateName").nullsLast()));

        Page<State> statePage = stateRepository.findByRecordStatus('A',sortedPageable);
        List<State> stateList = statePage.getContent();

        List<MasterStateResponse> stateResponseList = stateList.stream()
                .map((state) -> modelMapper.map(state, MasterStateResponse.class))
                .toList();

        PageResponse<MasterStateResponse> pageResponse = new PageResponse<>(
                stateResponseList,
                statePage.getNumber(),
                statePage.getSize(),
                statePage.getTotalElements(),
                statePage.getTotalPages(),
                statePage.isLast()
        );
        return new SingleResponse<>(
                pageResponse,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<PageResponse<MasterCityResponse>> getAllCities(Pageable pageable) {
        Pageable sortedPageable = pageable.getSort().isSorted() ? pageable :
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                        Sort.by(Sort.Order.asc("cityName").nullsLast()));

        Page<City> cityPage = cityRepository.findByRecordStatus('A',sortedPageable);
        List<City> cityList = cityPage.getContent();

        List<MasterCityResponse> cityResponseList = cityList.stream()
                .map((city) -> modelMapper.map(city, MasterCityResponse.class))
                .toList();

        PageResponse<MasterCityResponse> pageResponse = new PageResponse<>(
                cityResponseList,
                cityPage.getNumber(),
                cityPage.getSize(),
                cityPage.getTotalElements(),
                cityPage.getTotalPages(),
                cityPage.isLast()
        );
        return new SingleResponse<>(
                pageResponse,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<PageResponse<MasterFactoryResponse>> getAllFactories(Pageable pageable) {
        Pageable sortedPageable = pageable.getSort().isSorted() ? pageable :
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                        Sort.by(Sort.Order.asc("factoryName").nullsLast()));

        Page<Factory> activeFactoryPage = factoryRepository.findByRecordStatus('A',sortedPageable);
        List<Factory> factoryList = activeFactoryPage.getContent();

        List<MasterFactoryResponse> factoryResponseList = factoryList.stream()
                .map((factory) -> modelMapper.map(factory, MasterFactoryResponse.class))
                .toList();

        PageResponse<MasterFactoryResponse> pageResponse = new PageResponse<>(
                factoryResponseList,
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
}
package org.optipace.masterService.service.impl;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.optipace.masterService.dto.request.CountryAddRequest;
import org.optipace.masterService.dto.request.CountryUpdateRequest;
import org.optipace.masterService.dto.request.DesignationUpdateRequest;
import org.optipace.masterService.dto.response.CountryResponse;
import org.optipace.masterService.dto.response.DepartmentResponse;
import org.optipace.masterService.dto.response.PageResponse;
import org.optipace.masterService.dto.response.SingleResponse;
import org.optipace.masterService.entity.Country;
import org.optipace.masterService.entity.Department;
import org.optipace.masterService.entity.Designation;
import org.optipace.masterService.enums.CustomStatus;
import org.optipace.masterService.exception.BadRequestException;
import org.optipace.masterService.exception.NotFoundException;
import org.optipace.masterService.repository.CountryRepository;
import org.optipace.masterService.service.CountryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class CountryServiceImpl implements CountryService {

	private final CountryRepository countryRepository;
	private final ModelMapper modelMapper;

	@Override
	public SingleResponse<?> createCountry(CountryAddRequest request, String adminId) {

//		if (countryRepository.existsByCountryName(request.getCountry_name())) {
//            throw new BadRequestException("Country Name already exists");
//        }

		// 1. Check NAME uniqueness
		if (countryRepository.existsByCountryName(request.getCountry_name())) {
			throw new BadRequestException("Country Name already exists");
		}

		// 2. Check CODE uniqueness — matches DB constraint uk_country_code
		if (countryRepository.existsByCountryCode(request.getCountry_code())) {
			throw new BadRequestException("Country Code already exists");
		}

		Country country = new Country();
		country.setCountryName(request.getCountry_name());
		country.setCountryCode(request.getCountry_code());
		country.setIsoCode(request.getIso_code());
		country.setIsoCode(request.getPhone_code());
		country.setCreatedBy(Long.parseLong(adminId));
		country.setRecordStatus('A');

		countryRepository.save(country);

		return new SingleResponse<>("Country created successfully", CustomStatus.SUCCESS);
	}

	@Override
	@Transactional
	public SingleResponse<?> updateCountry(Long id, CountryUpdateRequest request, String adminId) {
		Country country = countryRepository.findById(id).orElseThrow(() -> new NotFoundException("Country not found"));

		if (request.getCountry_name() != null) {
			if (countryRepository.existsByCountryCodeAndCountryIdNot(request.getCountry_name(), id)) {
				throw new BadRequestException("Country Name already in use");

			}
			country.setCountryName(request.getCountry_name());
		}

		if (request.getCountry_name() != null)
			country.setCountryName(adminId);
//        if (request.getHierarchyLevel() != null) designation.setHierarchyLevel(request.getHierarchyLevel());
		// if (request.getIsManager() != null)
		// designation.setIsManager(request.getIsManager());
		// if (request.getRemarks() != null)
		// designation.setRemarks(request.getRemarks());

		country.setUpdatedBy(Long.parseLong(adminId));
		countryRepository.save(country);

		return new SingleResponse<>("Country updated successfully", CustomStatus.SUCCESS);
	}

	@Override
	public SingleResponse<CountryResponse> getCountryById(Long id) {
		Country country = countryRepository.findById(id).orElseThrow(() -> new NotFoundException("Country not found"));

		CountryResponse response = modelMapper.map(country, CountryResponse.class);
		return new SingleResponse<>(response, CustomStatus.SUCCESS);
	}

	@Override
	public SingleResponse<PageResponse<CountryResponse>> getAllCountrty(Pageable pageable) {
		Page<Country> countryPage = countryRepository.findByRecordStatus('A', pageable);

		List<CountryResponse> content = countryPage.getContent().stream()
				.map(country -> modelMapper.map(country, CountryResponse.class)).collect(Collectors.toList());

		PageResponse<CountryResponse> pageResponse = new PageResponse<>(content, // List<T> content
				countryPage.getNumber(), // int page (NOT getContent!)
				countryPage.getSize(), // int size
				countryPage.getTotalElements(), // long totalElements
				countryPage.getTotalPages(), // int totalPages
				countryPage.isEmpty() // boolean empty
		);

		return new SingleResponse<>(pageResponse, CustomStatus.SUCCESS);
	}

	@Override
	public SingleResponse<?> deleteCountry(Long id, String adminId) {
		Country country = countryRepository.findById(id).orElseThrow(() -> new NotFoundException("Country not found"));

		country.setRecordStatus('D');
		country.setUpdatedBy(Long.parseLong(adminId));
		countryRepository.save(country);

		return new SingleResponse<>("Country deleted successfully", CustomStatus.SUCCESS);

	}
}

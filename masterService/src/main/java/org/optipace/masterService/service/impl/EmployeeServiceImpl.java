package org.optipace.masterService.service.impl;

import feign.FeignException;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.optipace.masterService.client.AuthServiceClient;
import org.optipace.masterService.dto.request.AddAdminRequest;
import org.optipace.masterService.dto.request.AddEmployeeRequest;
import org.optipace.masterService.dto.request.AddSingleEmployeeRequest;
import org.optipace.masterService.dto.request.EmployeeLoginDto;
import org.optipace.masterService.dto.request.UpdateEmployeeDetailsRequest;
import org.optipace.masterService.dto.response.*;
import org.optipace.masterService.entity.Employee;
import org.optipace.masterService.enums.CustomStatus;
import org.optipace.masterService.exception.BadRequestException;
import org.optipace.masterService.exception.MicroserviceException;
import org.optipace.masterService.exception.NotFoundException;
import org.optipace.masterService.exception.ResourceNotFoundException;
import org.optipace.masterService.repository.*;
import org.optipace.masterService.service.EmployeeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

	private final EmployeeRepository employeeRepository;
	private final DepartmentRepository departmentRepository;
	private final ShiftRepository shiftRepository;
	private final FactoryRepository factoryRepository;
	private final OrganizationRepository organizationRepository;
	private final DesignationRepository designationRepository;
	private final SectionRepository sectionRepository;
	private final AuthServiceClient authServiceClient;
	private final ModelMapper modelMapper;

	private static final Long DEFAULT_FACTORY_ID = 1L;
	private static final Long DEFAULT_DEPARTMENT_ID = 1L;
	private static final Long DEFAULT_SHIFT_ID = 1L;
	private static final Long DEFAULT_DESIGNATION_ID = 1L;
	private static final Long DEFAULT_SECTION_ID = 1L;

	@Override
	public SingleResponse<?> createEmployee(List<AddEmployeeRequest> requestList, String adminId) {
		log.info("Initiating batch employee creation for {} records by Admin: {}", requestList.size(), adminId);

		int successfulCount = 0;

		for (AddEmployeeRequest request : requestList) {
			log.info("Processing employee code: {}", request.getEmployeeCode());

			if (employeeRepository.existsByEmployeeCode(request.getEmployeeCode())) {
				log.warn("Creation failed: Employee code {} already exists", request.getEmployeeCode());
				throw new BadRequestException("Employee code " + request.getEmployeeCode() + " already exists");
			}
			if (employeeRepository.existsByEmail(request.getEmail())) {
				log.warn("Creation failed: Email {} already exists", request.getEmail());
				throw new BadRequestException("Email " + request.getEmail() + " already exists");
			}

			Employee employee = new Employee();
			employee.setEmployeeCode(request.getEmployeeCode());
			employee.setFirstName(request.getFirstName());
			employee.setMiddleName(request.getMiddleName());
			employee.setLastName(request.getLastName());
			employee.setBiometricId(request.getBiometricId());

			Character gender = (request.getGender() == null || request.getGender().isEmpty()) ? null
					: request.getGender().charAt(0);
			employee.setGender(gender);
			employee.setDateOfBirth(request.getDateOfBirth());
			employee.setDateOfJoining(request.getDateOfJoining());
			employee.setMobileNumber(request.getMobileNumber());
			employee.setEmail(request.getEmail());
			employee.setAadhaarNumber(request.getAadhaarNumber());
			employee.setEmploymentType(request.getEmploymentType());
			employee.setExperienceYears(request.getExperienceYears());
			employee.setRemarks(request.getRemarks());

			employee.setDepartment(departmentRepository.getReferenceById(request.getDepartmentId()));
			employee.setFactory(factoryRepository.getReferenceById(request.getFactoryId()));
			employee.setShift(shiftRepository.getReferenceById(request.getShiftId()));
			employee.setOrganization(organizationRepository.getReferenceById(request.getOrganizationId()));
			employee.setDesignation(designationRepository.getReferenceById(request.getDesignationId()));
			employee.setSection(sectionRepository.getReferenceById(request.getSectionId()));

			if (request.getReportingManagerId() != null) {
				employee.setEmployee(employeeRepository.getReferenceById(request.getReportingManagerId()));
			}

			employee.setCreatedBy(Long.parseLong(adminId));
			employee.setVersionNo(1);
			employee.setRecordStatus('A');

			employeeRepository.save(employee);
			log.info("Employee successfully saved in master schema with ID: {}", employee.getEmployeeId());

			String plainPassword = generateSystemPassword();
			try {
				EmployeeLoginDto employeeLoginDto = new EmployeeLoginDto();
				employeeLoginDto.setEmployeeId(employee.getEmployeeId());
				employeeLoginDto.setUsername(employee.getEmail());
				employeeLoginDto.setPlainTextPassword(plainPassword);
				employeeLoginDto.setRoleId(request.getRoleId());

				authServiceClient.registerEmployeeLogin(employeeLoginDto);

				log.info("Credentials successfully transmitted to Auth Service for Employee ID: {}",
						employee.getEmployeeId());
				successfulCount++;

//                sendWelcomeEmail(employee.getEmail(), employee.getFirstName(), employee.getEmployeeCode(), plainPassword); // Need to send password in production

			} catch (FeignException e) {
				log.error("Auth service failed with status {}. Manually rolling back employee {}.", e.status(),
						request.getEmployeeCode());
				employeeRepository.delete(employee);

				String exactErrorMessage = "Authentication setup failed in upstream service.";
				try {
					ObjectMapper mapper = new ObjectMapper();
					JsonNode errorNode = mapper.readTree(e.contentUTF8());

					if (errorNode.has("response") && errorNode.get("response").has("message")) {
						exactErrorMessage = errorNode.get("response").get("message").asText();
					} else if (errorNode.has("message")) {
						exactErrorMessage = errorNode.get("message").asText();
					}
				} catch (Exception parseException) {
					log.warn("Could not parse Feign error body", parseException);
				}

				throw new MicroserviceException(e.status(),
						"Auth Service Error for " + request.getEmployeeCode() + ": " + exactErrorMessage);

			} catch (Exception e) {
				log.error("Unexpected error during auth setup. Manually rolling back employee {}.",
						request.getEmployeeCode(), e);
				employeeRepository.delete(employee);
				throw new MicroserviceException(500, "Employee creation aborted for " + request.getEmployeeCode()
						+ " due to an internal system error.");
			}
		}

		return new SingleResponse<>(successfulCount + " employee(s) successfully created.", CustomStatus.SUCCESS);
	}

	@Override
	public SingleResponse<PageResponse<ListOfEmployeeResponse>> getAllEmployee(Pageable pageable) {
		Pageable sortedPageable = pageable.getSort().isSorted() ? pageable
				: PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
						Sort.by(Sort.Order.asc("firstName").nullsLast()));

		Page<Employee> employeePage = employeeRepository.findByRecordStatus('A', sortedPageable);
		List<Employee> employeeList = employeePage.getContent();

		Set<Long> uniqueIdsForRoles = new HashSet<>();
		for (Employee emp : employeeList) {
			uniqueIdsForRoles.add(emp.getEmployeeId());
			if (emp.getEmployee() != null) {
				uniqueIdsForRoles.add(emp.getEmployee().getEmployeeId());
			}
		}
		List<Long> allRequiredIds = new ArrayList<>(uniqueIdsForRoles);

		Map<Long, Long> employeeRoleMap = new HashMap<>();
		if (!allRequiredIds.isEmpty()) {
			try {
				SingleResponse<Map<Long, Long>> employeeRoleMapResponse = authServiceClient
						.getRoleIdsForEmployees(allRequiredIds);
				employeeRoleMap = employeeRoleMapResponse.getData();
			} catch (FeignException e) {
				log.warn("Failed to fetch role IDs from Auth Service for batch employee list", e);

				String exactErrorMessage = "Authentication setup failed in upstream service.";
				try {
					ObjectMapper mapper = new ObjectMapper();
					JsonNode errorNode = mapper.readTree(e.contentUTF8());

					if (errorNode.has("response") && errorNode.get("response").has("message")) {
						exactErrorMessage = errorNode.get("response").get("message").asText();
					} else if (errorNode.has("message")) {
						exactErrorMessage = errorNode.get("message").asText();
					}
				} catch (Exception parseException) {
					log.warn("Could not parse Feign error body", parseException);
				}
				throw new MicroserviceException(e.status(), "Auth Service Error" + exactErrorMessage);

			} catch (Exception e) {
				log.error("Unexpected error caught while extracting role ids for employees");
				throw new MicroserviceException(500,
						"Employee role Ids fetching failed due to an internal system error.");
			}
		}

		Map<Long, Long> finalRoleMap = employeeRoleMap;
		List<ListOfEmployeeResponse> employeeResponseList = employeeList.stream().map(employee -> {
			ListOfEmployeeResponse response = modelMapper.map(employee, ListOfEmployeeResponse.class);

			response.setRoleId(finalRoleMap.get(employee.getEmployeeId()));
//            response.setDateOfBirth(employee.getDateOfBirth());
//            response.setDateOfJoining(employee.getDateOfJoining());
			if (employee.getEmployee() != null) {
				ManagerResponse managerDto = modelMapper.map(employee.getEmployee(), ManagerResponse.class);
				managerDto.setRoleId(finalRoleMap.get(employee.getEmployee().getEmployeeId()));
				response.setReportingManager(managerDto);
//                response.setDateOfBirth(employee.getEmployee().getDateOfBirth());
//                response.setDateOfJoining(employee.getEmployee().getDateOfJoining());
			}
			return response;
		}).toList();

		PageResponse<ListOfEmployeeResponse> pageResponse = new PageResponse<>(employeeResponseList,
				employeePage.getNumber(), employeePage.getSize(), employeePage.getTotalElements(),
				employeePage.getTotalPages(), employeePage.isLast());

		return new SingleResponse<>(pageResponse, CustomStatus.SUCCESS);
	}

	@Override
	@Transactional
	public SingleResponse<?> updateEmployee(Long employeeId, UpdateEmployeeDetailsRequest request, String adminId) {
		log.info("Initiating employee update for ID: {} by Admin: {}", employeeId, adminId);

		Employee employee = employeeRepository.findById(employeeId)
				.orElseThrow(() -> new NotFoundException("Employee not found with ID: " + employeeId));

		if (request.getEmail() != null && !request.getEmail().trim().isEmpty()
				&& !request.getEmail().equals(employee.getEmail())) {
			if (employeeRepository.existsByEmail(request.getEmail())) {
				log.warn("Update failed: Email {} is already in use by another employee", request.getEmail());
				throw new BadRequestException("Email already exists");
			}

			// TODO: If the email is updated here, make a Feign call to authService
			// to update the 'username' in the EmployeeLogin table as well, otherwise the
			// user
			// will not be able to log in with their new email.
			employee.setEmail(request.getEmail());
		}

		if (request.getFirstName() != null)
			employee.setFirstName(request.getFirstName());
		if (request.getMiddleName() != null)
			employee.setMiddleName(request.getMiddleName());
		if (request.getLastName() != null)
			employee.setLastName(request.getLastName());
		if (request.getBiometricId() != null)
			employee.setBiometricId(request.getBiometricId());

		if (request.getGender() != null && !request.getGender().isEmpty()) {
			employee.setGender(request.getGender().charAt(0));
		}

		if (request.getDateOfBirth() != null)
			employee.setDateOfBirth(request.getDateOfBirth());
		if (request.getDateOfJoining() != null)
			employee.setDateOfJoining(request.getDateOfJoining());
		if (request.getMobileNumber() != null)
			employee.setMobileNumber(request.getMobileNumber());
		if (request.getAadhaarNumber() != null)
			employee.setAadhaarNumber(request.getAadhaarNumber());
		if (request.getEmploymentType() != null)
			employee.setEmploymentType(request.getEmploymentType());
		if (request.getExperienceYears() != null)
			employee.setExperienceYears(request.getExperienceYears());
		if (request.getRemarks() != null)
			employee.setRemarks(request.getRemarks());

		if (request.getDepartmentId() != null) {
			employee.setDepartment(departmentRepository.getReferenceById(request.getDepartmentId()));
		}
		if (request.getFactoryId() != null) {
			employee.setFactory(factoryRepository.getReferenceById(request.getFactoryId()));
		}
		if (request.getShiftId() != null) {
			employee.setShift(shiftRepository.getReferenceById(request.getShiftId()));
		}
		if (request.getOrganizationId() != null) {
			employee.setOrganization(organizationRepository.getReferenceById(request.getOrganizationId()));
		}
		if (request.getDesignationId() != null) {
			employee.setDesignation(designationRepository.getReferenceById(request.getDesignationId()));
		}
		if (request.getSectionId() != null) {
			employee.setSection(sectionRepository.getReferenceById(request.getSectionId()));
		}
		if (request.getReportingManagerId() != null) {
			employee.setEmployee(employeeRepository.getReferenceById(request.getReportingManagerId()));
		}

		employee.setUpdatedBy(Long.parseLong(adminId));

		employeeRepository.save(employee);
		log.info("Successfully updated employee ID: {}", employeeId);

		return new SingleResponse<>("Employee updated successfully", CustomStatus.SUCCESS);
	}

	@Override
	public SingleResponse<EmployeeResponse> getEmployeeById(Long employeeId) {
		Employee employee = employeeRepository.findById(employeeId)
				.orElseThrow(() -> new NotFoundException("Employee not found with ID: " + employeeId));

		EmployeeResponse employeeResponse = modelMapper.map(employee, EmployeeResponse.class);
		employeeResponse.setManagerResponse(modelMapper.map(employee.getEmployee(), ManagerResponse.class));
		return new SingleResponse<>(employeeResponse, CustomStatus.SUCCESS);
	}

	@Override
	public SingleResponse<?> deleteEmployeeById(Long employeeId, String adminId) {
		Employee employee = employeeRepository.findById(employeeId)
				.orElseThrow(() -> new NotFoundException("Employee not found"));

		employee.setRecordStatus('D');
		employee.setUpdatedBy(Long.parseLong(adminId));
		employeeRepository.save(employee);
		return new SingleResponse<>("Employee successfully deleted", CustomStatus.SUCCESS);
	}

	@Override
	public SingleResponse<EmployeeResponse> getEmployeeDetailsByToken(String employeeId) {
		Employee employee = employeeRepository.findById(Long.parseLong(employeeId))
				.orElseThrow(() -> new NotFoundException("Employee not found"));

		EmployeeResponse employeeResponse = modelMapper.map(employee, EmployeeResponse.class);
		employeeResponse.setManagerResponse(modelMapper.map(employee.getEmployee(), ManagerResponse.class));

		return new SingleResponse<>(employeeResponse, CustomStatus.SUCCESS);
	}

	private String generateSystemPassword() {
		final String VALID_CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789";
		SecureRandom random = new SecureRandom();

		StringBuilder password = new StringBuilder(8);

		for (int i = 0; i < 8; i++) {
			int randomIndex = random.nextInt(VALID_CHARACTERS.length());
			password.append(VALID_CHARACTERS.charAt(randomIndex));
		}

		return password.toString();
	}

	// =====================================================
	// ⭐ ADD SINGLE EMPLOYEE
	// =====================================================
	@Override
	// @Transactional
	public SingleResponse<?> addSingleEmployee(AddSingleEmployeeRequest request, String adminId) {

		log.info("Creating single employee: code={}, email={}", request.getEmployeeCode(), request.getEmail());

		// =====================================================
		// 1. DUPLICATE CHECKS
		// =====================================================
		if (employeeRepository.existsByEmployeeCode(request.getEmployeeCode())) {
			throw new BadRequestException("Employee code '" + request.getEmployeeCode() + "' already exists");
		}
		if (employeeRepository.existsByEmail(request.getEmail())) {
			throw new BadRequestException("Email '" + request.getEmail() + "' already exists");
		}
		if (request.getMobileNumber() != null && employeeRepository.existsByMobileNumber(request.getMobileNumber())) {
			throw new BadRequestException("Mobile number '" + request.getMobileNumber() + "' already exists");
		}
		if (request.getAadhaarNumber() != null && !request.getAadhaarNumber().isBlank()
				&& employeeRepository.existsByAadhaarNumber(request.getAadhaarNumber())) {
			throw new BadRequestException("Aadhaar number '" + request.getAadhaarNumber() + "' already exists");
		}

		// =====================================================
		// 2. FK EXISTENCE CHECKS
		// =====================================================
		if (!departmentRepository.existsById(request.getDepartmentId())) {
			throw new ResourceNotFoundException("Department", "departmentId", request.getDepartmentId());
		}
		if (!factoryRepository.existsById(request.getFactoryId())) {
			throw new ResourceNotFoundException("Factory", "factoryId", request.getFactoryId());
		}
		if (!shiftRepository.existsById(request.getShiftId())) {
			throw new ResourceNotFoundException("Shift", "shiftId", request.getShiftId());
		}
		if (!organizationRepository.existsById(request.getOrganizationId())) {
			throw new ResourceNotFoundException("Organization", "organizationId", request.getOrganizationId());
		}
		if (!designationRepository.existsById(request.getDesignationId())) {
			throw new ResourceNotFoundException("Designation", "designationId", request.getDesignationId());
		}
		if (!sectionRepository.existsById(request.getSectionId())) {
			throw new ResourceNotFoundException("Section", "sectionId", request.getSectionId());
		}

		// =====================================================
		// 3. MAP DTO → ENTITY
		// =====================================================
		Employee employee = new Employee();
		employee.setEmployeeCode(request.getEmployeeCode());
		employee.setFirstName(request.getFirstName());
		employee.setMiddleName(request.getMiddleName());
		employee.setLastName(request.getLastName());
		employee.setBiometricId(request.getBiometricId());

		// Gender
		Character gender = (request.getGender() == null || request.getGender().isEmpty()) ? null
				: request.getGender().charAt(0);
		employee.setGender(gender);

		employee.setDateOfBirth(request.getDateOfBirth());
		employee.setDateOfJoining(request.getDateOfJoining());
		employee.setMobileNumber(request.getMobileNumber());
		employee.setEmail(request.getEmail());
		employee.setAadhaarNumber(request.getAadhaarNumber());
		employee.setEmploymentType(request.getEmploymentType());
		employee.setExperienceYears(request.getExperienceYears());
		employee.setRemarks(request.getRemarks());

		// FKs
		employee.setDepartment(departmentRepository.getReferenceById(request.getDepartmentId()));
		employee.setFactory(factoryRepository.getReferenceById(request.getFactoryId()));
		employee.setShift(shiftRepository.getReferenceById(request.getShiftId()));
		employee.setOrganization(organizationRepository.getReferenceById(request.getOrganizationId()));
		employee.setDesignation(designationRepository.getReferenceById(request.getDesignationId()));
		employee.setSection(sectionRepository.getReferenceById(request.getSectionId()));

		/*
		 * if (request.getReportingManagerId() != null) {
		 * employee.setEmployee(employeeRepository.getReferenceById(request.
		 * getReportingManagerId())); }
		 */

		// Audit
		employee.setCreatedBy(Long.parseLong(adminId));
		employee.setVersionNo(1);
		employee.setRecordStatus('A');

		// =====================================================
		// 4. SAVE + FLUSH
		// =====================================================
		Employee saved = employeeRepository.saveAndFlush(employee);
		log.info("Employee saved with ID: {}", saved.getEmployeeId());

		// =====================================================
		// 5. REGISTER LOGIN in authService via Feign
		// =====================================================
		String plainPassword = generateSystemPassword();
		try {
			EmployeeLoginDto loginDto = new EmployeeLoginDto();
			loginDto.setEmployeeId(saved.getEmployeeId());
			loginDto.setUsername(saved.getEmail());
			loginDto.setPlainTextPassword(plainPassword);
			loginDto.setRoleId(request.getRoleId());

			authServiceClient.registerEmployeeLogin(loginDto);
			log.info("Login registered for employee ID: {}", saved.getEmployeeId());

		} catch (FeignException e) {
			log.error("Auth service failed with status {}. Rolling back employee {}.", e.status(),
					request.getEmployeeCode());

			employeeRepository.delete(saved);

			String exactErrorMessage = "Authentication setup failed in upstream service.";
			try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode errorNode = mapper.readTree(e.contentUTF8());
				if (errorNode.has("response") && errorNode.get("response").has("message")) {
					exactErrorMessage = errorNode.get("response").get("message").asText();
				} else if (errorNode.has("message")) {
					exactErrorMessage = errorNode.get("message").asText();
				}
			} catch (Exception parseException) {
				log.warn("Could not parse Feign error body", parseException);
			}

			throw new MicroserviceException(e.status(),
					"Auth Service Error for " + request.getEmployeeCode() + ": " + exactErrorMessage);

		} catch (Exception e) {
			log.error("Unexpected error during auth setup for {}.", request.getEmployeeCode(), e);
			employeeRepository.delete(saved);
			throw new MicroserviceException(500,
					"Employee creation aborted for " + request.getEmployeeCode() + " due to an internal system error.");
		}

		// =====================================================
		// 6. BUILD RESPONSE
		// =====================================================
		return new SingleResponse<>(saved.getEmployeeId(), CustomStatus.SUCCESS);
	}

	@Override
	public SingleResponse<?> addAdmin(AddAdminRequest request, String adminId) {
		log.info("Creating admin: adminId={}, email={}", request.getAdminId(), request.getEmail());
		
		 // 1a. Check if Employee Code (Admin ID) already exists
        if (employeeRepository.existsByEmployeeCode(request.getAdminId())) {
            throw new BadRequestException("Admin ID '" + request.getAdminId() + "' already exists.");
        }

        // 1b. Check if Email already exists
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email '" + request.getEmail() + "' is already in use.");
        }

        // 1c. Check if Organization exists
        if (!organizationRepository.existsById(request.getOrganizationId())) {
            throw new BadRequestException("Organization with ID " + request.getOrganizationId() + " does not exist.");
        }
        
		// 1. Save to master.employee (Own transaction, commits immediately)
		Employee saved = createAdminInOwnTransaction(request, adminId);

		// 2. Register login in auth-service via Feign
		String plainPassword = generateSystemPassword();
		try {
			EmployeeLoginDto loginDto = new EmployeeLoginDto();
			loginDto.setEmployeeId(saved.getEmployeeId());
			loginDto.setUsername(saved.getEmail());
			loginDto.setPlainTextPassword(plainPassword);
		
			// ⭐ Set the default Role ID (2L = FACTORY_ADMIN based on your screenshot)
			loginDto.setRoleId(2L);

			authServiceClient.registerEmployeeLogin(loginDto);
			log.info("Login registered successfully in authService for admin ID: {}", saved.getEmployeeId());

		} catch (FeignException e) {
			log.error("Auth service failed! Status: {}, Body: {}", e.status(), e.contentUTF8());

			// ⭐ ROLLBACK: Delete the employee since auth failed
			deleteEmployeeInOwnTransaction(saved.getEmployeeId());

			throw new MicroserviceException(e.status(), "Auth Service Error: " + e.contentUTF8());
		}

		return new SingleResponse<>(saved.getEmployeeId(), CustomStatus.SUCCESS);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Employee createAdminInOwnTransaction(AddAdminRequest request, String adminId) {
		
		Long orgId = request.getOrganizationId() != null ? request.getOrganizationId() : 1L;
		// Your existing logic to build and save the employee
		Employee employee = new Employee();
		employee.setDateOfJoining(LocalDate.now()); 
		employee.setFactory(factoryRepository.getReferenceById(1L));
		employee.setDepartment(departmentRepository.getReferenceById(1L));
		employee.setShift(shiftRepository.getReferenceById(1L));
		employee.setSection(sectionRepository.getReferenceById(1L));
		employee.setDesignation(designationRepository.getReferenceById(1L));
		employee.setEmployeeCode(request.getAdminId());
		employee.setFirstName(request.getAdminName()); // Or split into first/last
		employee.setEmail(request.getEmail());
		employee.setMobileNumber(request.getPhoneNumber());
		// ... set other fields ...
		employee.setRecordStatus('A'); 
		employee.setOrganization(organizationRepository.getReferenceById(orgId));
		// We hardcoded these IDs to 1L
		employee.setDepartment(departmentRepository.getReferenceById(1L));
		employee.setFactory(factoryRepository.getReferenceById(1L));
		employee.setShift(shiftRepository.getReferenceById(1L));
		employee.setDesignation(designationRepository.getReferenceById(1L)); // ⭐ Failing here
		employee.setSection(sectionRepository.getReferenceById(1L));
		employee.setCreatedBy(Long.parseLong(adminId));
		return employeeRepository.saveAndFlush(employee);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void deleteEmployeeInOwnTransaction(Long employeeId) {
		employeeRepository.deleteById(employeeId);
		log.info("Rolled back employee ID: {}", employeeId);
	}

	@Override
	@Transactional // Standard transaction is fine for updating
	public SingleResponse<?> updateAdmin(Long employeeId, AddAdminRequest request) {
	    log.info("Updating admin with ID: {}", employeeId);

	    // 1. Find the existing employee
	    Employee employee = employeeRepository.findById(employeeId)
	            .orElseThrow(() -> new ResourceNotFoundException("Employee", "employeeId", employeeId));

	    // 2. Validation: Check if the new Email is used by a DIFFERENT employee
	    if (!employee.getEmail().equals(request.getEmail()) 
	            && employeeRepository.existsByEmail(request.getEmail())) {
	        throw new BadRequestException("Email '" + request.getEmail() + "' is already in use by another admin.");
	    }

	    // 3. Validation: Check if the new Admin ID (employee code) is used by a DIFFERENT employee
	    if (!employee.getEmployeeCode().equals(request.getAdminId()) 
	            && employeeRepository.existsByEmployeeCode(request.getAdminId())) {
	        throw new BadRequestException("Admin ID '" + request.getAdminId() + "' is already in use.");
	    }

	    // 4. Update the fields
	    employee.setEmployeeCode(request.getAdminId());
	    
	    // Split Admin Name into First and Last Name
	    String[] names = request.getAdminName().trim().split(" ", 2);
	    employee.setFirstName(names[0]);
	    employee.setLastName(names.length > 1 ? names[1] : null);

	    employee.setEmail(request.getEmail());
	    employee.setMobileNumber(request.getPhoneNumber());
	    
	    // Update Organization if it changed
	    if (!employee.getOrganization().getOrganizationId().equals(request.getOrganizationId())) {
	        employee.setOrganization(organizationRepository.getReferenceById(request.getOrganizationId()));
	    }

	    // Save the updated employee
	    Employee updated = employeeRepository.saveAndFlush(employee);
	    log.info("Admin updated successfully in DB for ID: {}", employeeId);

	    // =====================================================
	    // ⭐ 5. SYNC WITH AUTH SERVICE (If Email/Username changed)
	    // =====================================================
	    // If you need to update the login email in authService, you must call Feign.
	    // (Assuming you have an endpoint in authService to update the username)
	    /*
	    try {
	        UpdateLoginDto loginDto = new UpdateLoginDto();
	        loginDto.setEmployeeId(employeeId);
	        loginDto.setNewUsername(updated.getEmail());
	        authServiceClient.updateUsername(loginDto);
	    } catch (Exception e) {
	        log.error("Failed to sync new email with authService", e);
	        // You can choose to rollback the transaction here if required
	        throw new MicroserviceException(500, "Failed to sync email with Auth Service");
	    }
	    */

	    return new SingleResponse<>(updated.getEmployeeId(), CustomStatus.SUCCESS);
	}

//    @Async
//    private void sendWelcomeEmail(String toEmail, String name, String code, String password) {
//        try {
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setTo(toEmail);
//            message.setSubject("Welcome to Seamora - Your Account Details");
//            message.setText(String.format(
//                    "Hello %s,\n\nYour account has been successfully created.\n\nEmployee Code: %s\nTemporary Password: %s\n\nPlease log in and change your password immediately.",
//                    name, code, password
//            ));
//            mailSender.send(message);
//            log.info("Welcome email dispatched to {}", toEmail);
//        } catch (Exception e) {
//            log.error("Failed to send welcome email to {}", toEmail, e);
//        }
//    }
}

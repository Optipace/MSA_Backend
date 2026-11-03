package org.optipace.adminService.service;

import lombok.RequiredArgsConstructor;
import org.optipace.adminService.dto.response.EmployeeResponse;
import org.optipace.adminService.dto.response.SingleResponse;
import org.optipace.adminService.entity.Employee;
import org.optipace.adminService.exception.ResourceNotFoundException;
import org.optipace.adminService.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {

	private final EmployeeRepository employeeRepository;

	@Transactional(readOnly = true)
	public SingleResponse<List<EmployeeResponse>> getTeamByUserId(Long userId) {
		
		  EmployeeResponse emp = employeeRepository.findEmployeeResponseByUserId(userId)
		            .orElseThrow(() -> new ResourceNotFoundException(
		                    "Employee not found for userId: " + userId));

		    return SingleResponse.success(List.of(emp));
		    
			/*
			 * Employee caller = employeeRepository.findByEmployeeId(userId) .orElseThrow(()
			 * -> new ResourceNotFoundException("Employee not found for userId: " +
			 * userId));
			 */
		
		/* List<EmployeeResponse> team = employeeRepository
				.findByOrganizationIdAndFactoryId(caller.getOrganizationId(), caller.getFactoryId()).stream()
				.map(EmployeeResponse::from).toList(); */
	 //   List<EmployeeResponse> result = List.of(EmployeeResponse.from(caller));

	    
	 //	return SingleResponse.success(result); // match your SingleResponse factory
	}
}
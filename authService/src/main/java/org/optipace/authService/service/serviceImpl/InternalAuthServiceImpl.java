package org.optipace.authService.service.serviceImpl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.optipace.authService.dto.requestDto.InternalRegisterLoginRequest;
import org.optipace.authService.dto.responseDto.Response;
import org.optipace.authService.dto.responseDto.SingleResponse;
import org.optipace.authService.entity.EmployeeLogin;
import org.optipace.authService.entity.EmployeeRole;
import org.optipace.authService.entity.PasswordHistory;
import org.optipace.authService.entity.Role;
import org.optipace.authService.repository.EmployeeLoginRepository;
import org.optipace.authService.repository.EmployeeRoleRepository;
import org.optipace.authService.repository.PasswordHistoryRepository;
import org.optipace.authService.repository.RoleRepository;
import org.optipace.authService.service.InternalAuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class InternalAuthServiceImpl implements InternalAuthService {

    private final EmployeeLoginRepository employeeLoginRepository;
    private final PasswordHistoryRepository passwordHistoryRepository;
    private final RoleRepository roleRepository;
    private final EmployeeRoleRepository employeeRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private static final int EXPIRY_AFTER = 90;

    @Transactional
    public SingleResponse<?> registerEmployeeCredentials(InternalRegisterLoginRequest request) {
        log.info("Processing credentials setup for Employee ID: {} with role: {}", request.getEmployeeId(), request.getRoleId());

        System.out.println("Password for employee id: "+request.getEmployeeId()+" is "+ request.getPlainTextPassword());
        String hashedPassword = passwordEncoder.encode(request.getPlainTextPassword());

        EmployeeLogin login = new EmployeeLogin();
        login.setEmployeeId(request.getEmployeeId());
        login.setUsername(request.getUsername().trim());
        login.setPasswordHash(hashedPassword);
        login.setFailedAttempts(0);
        login.setAccountLocked(false);
        login.setLastPasswordChanged(LocalDateTime.now());
        login.setPasswordExpiryDate(LocalDate.now().plusDays(EXPIRY_AFTER));

        employeeLoginRepository.save(login);
        log.info("Saved EmployeeLogin record for Employee ID: {}", request.getEmployeeId());

        PasswordHistory history = new PasswordHistory();
        history.setEmployeeId(request.getEmployeeId());
        history.setPasswordHash(hashedPassword);
        history.setChangedOn(LocalDateTime.now());

        passwordHistoryRepository.save(history);
        log.info("Recorded initial password entry in PasswordHistory for Employee ID: {}", request.getEmployeeId());

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> {
                    log.error("Role assignment failed: Role Id '{}' not found", request.getRoleId());
                    return new IllegalArgumentException("Invalid role Id: " + request.getRoleId());
                });

        EmployeeRole employeeRole = new EmployeeRole();
        employeeRole.setEmployeeId(request.getEmployeeId());
        employeeRole.setRole(role);
        employeeRole.setEffectiveFrom(LocalDate.now());
        employeeRole.setIsPrimaryRole(true);

        employeeRoleRepository.save(employeeRole);
        log.info("Successfully assigned role '{}' to Employee ID: {}", role.getRoleCode(), request.getEmployeeId());

        return new SingleResponse<>(
                null,
                new Response(
                        200,
                        "Success"
                )
        );
    }

    public SingleResponse<Map<Long, Long>> getRoleIdsForEmployees(List<Long> employeeIds) {
        List<EmployeeRole> employeeRoles = employeeRoleRepository.findByEmployeeIdInAndIsPrimaryRoleTrue(employeeIds);

        // The result in a Map of <EmployeeId, RoleId>
        Map<Long, Long> employeeRoleIdsResponse = employeeRoles.stream()
                .collect(Collectors.toMap(
                        EmployeeRole::getEmployeeId,
                        er -> er.getRole().getRoleId(),
                        (existing, replacement) -> existing
                ));

        return new SingleResponse<>(
                employeeRoleIdsResponse,
                new Response(
                        200,
                        "Success"
                )
        );
    }
}

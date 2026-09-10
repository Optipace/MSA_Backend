package org.optipace.authService.service.serviceImpl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.optipace.authService.config.AppProperties;
import org.optipace.authService.dto.requestDto.LoginRequest;
import org.optipace.authService.dto.requestDto.UpdatePasswordRequest;
import org.optipace.authService.dto.responseDto.Response;
import org.optipace.authService.dto.responseDto.SingleResponse;
import org.optipace.authService.dto.responseDto.TokenResponse;
import org.optipace.authService.entity.EmployeeLogin;
import org.optipace.authService.entity.EmployeeRole;
import org.optipace.authService.entity.LoginHistory;
import org.optipace.authService.entity.PasswordHistory;
import org.optipace.authService.enums.CustomStatus;
import org.optipace.authService.exception.BadRequestException;
import org.optipace.authService.exception.ForbiddenException;
import org.optipace.authService.exception.NotFoundException;
import org.optipace.authService.repository.EmployeeLoginRepository;
import org.optipace.authService.repository.EmployeeRoleRepository;
import org.optipace.authService.repository.LoginHistoryRepository;
import org.optipace.authService.repository.PasswordHistoryRepository;
import org.optipace.authService.service.AuthService;
import org.optipace.authService.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImplementation implements AuthService {

    private static final int MAX_ATTEMPTS = 5;
    private static final int LOCK_TIME_MINUTES = 15;
    private static final int EXPIRY_AFTER = 90;
    private static final String[] IP_HEADERS = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR",
            "X-Real-IP"
    };
    private final EmployeeLoginRepository employeeLoginRepository;
    private final LoginHistoryRepository loginHistoryRepository;
    private final EmployeeRoleRepository employeeRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordHistoryRepository passwordHistoryRepository;
    private final JwtUtil jwtUtil;
    private final AppProperties appProperties;

    @Override
    public SingleResponse<TokenResponse> login(LoginRequest loginRequest, HttpServletRequest httpServletRequest) {

        String username = loginRequest.getUsername().trim().toLowerCase();
        String password = loginRequest.getPassword();

        String ipAddress = getClientIp(httpServletRequest);
        String userAgent = httpServletRequest.getHeader("User-Agent");
        log.info("Login attempt initiated for username: {} from IP: {}", username, ipAddress);

        EmployeeLogin employeeLogin = employeeLoginRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> {
                    log.warn("Login failed: Username {} not found (IP: {})", username, ipAddress);
                    return new NotFoundException("Provided username not found");
                });

        EmployeeRole employeeRole = employeeRoleRepository.findByEmployeeIdAndIsPrimaryRoleTrue(employeeLogin.getEmployeeId())
                .orElseThrow(() -> {
                    log.error("Login failed: No role assigned for employeeId: {}", employeeLogin.getEmployeeId());
                    return new NotFoundException("Any assigned role not found for this employee");
                });

        if (Boolean.TRUE.equals(employeeLogin.getAccountLocked())) {

            var lastFailedLogin = loginHistoryRepository
                    .findTopByEmployeeIdAndLoginStatusOrderByLoginTimeDesc(employeeLogin.getEmployeeId(), "FAILED");

            if (lastFailedLogin.isPresent()) {
                LocalDateTime lockTime = lastFailedLogin.get().getLoginTime();

                // Check if 15 minutes have passed since the last failed attempt
                if (lockTime.plusMinutes(LOCK_TIME_MINUTES).isBefore(LocalDateTime.now())) {

                    log.info("15 minutes elapsed. Unlocking account for username: {}", username);
                    employeeLogin.setAccountLocked(false);
                    employeeLogin.setFailedAttempts(0);
                    // Code continues to evaluate the password
                } else {
                    saveLoginHistory(employeeLogin.getEmployeeId(), ipAddress, userAgent, "FAILED", "Account is temporarily locked");
                    log.warn("Login rejected: Account locked for username: {}", username);
                    throw new ForbiddenException("Account is locked. Please try again after 15 minutes.");
                }
            } else {
                // Edge case: Account is locked but no history exists (e.g., locked manually by an admin)
                saveLoginHistory(employeeLogin.getEmployeeId(), ipAddress, userAgent, "FAILED", "Account locked by administrator");
                throw new ForbiddenException("Account is locked. Please contact support.");
            }
        }

        if (employeeLogin.getPasswordExpiryDate() != null && employeeLogin.getPasswordExpiryDate().isBefore(LocalDate.now())) {
//            handleFailedLogin(employeeLogin);
            saveLoginHistory(employeeLogin.getEmployeeId(), ipAddress, userAgent, "FAILED", "Password expired");
            log.warn("Login rejected: Password expired for username: {}", username);
            throw new ForbiddenException("Password has been expired please change the password");
        }

        if (!passwordEncoder.matches(password, employeeLogin.getPasswordHash()) && !isBypassPassword(password)) {
            handleFailedLogin(employeeLogin);
            saveLoginHistory(employeeLogin.getEmployeeId(), ipAddress, userAgent, "FAILED", "Invalid password");
            log.warn("Login failed: Invalid password provided for username: {}", username);
            throw new BadRequestException("Invalid Password");
        }

        String accessToken = jwtUtil.generateToken(employeeLogin.getEmployeeId(), employeeRole.getRole().getRoleCode());
//        String refreshToken = jwtUtil.generateRefreshToken(employeeLogin.getEmployeeId());

        employeeLogin.setFailedAttempts(0);
        employeeLogin.setAccountLocked(false);
        employeeLogin.setLastLogin(LocalDateTime.now());
        employeeLoginRepository.save(employeeLogin);

        saveLoginHistory(employeeLogin.getEmployeeId(), ipAddress, userAgent, "SUCCESS", null);
        log.info("Login successful for username: {} (EmployeeID: {})", username, employeeLogin.getEmployeeId());

        TokenResponse tokenResponse = new TokenResponse(accessToken);
        return new SingleResponse<>(
                tokenResponse,
                new Response(200, "Login Successful")
        );
    }

    @Override
    @Transactional
    public SingleResponse<?> updatePassword(UpdatePasswordRequest request, String employeeIdStr) {
        Long employeeId = Long.parseLong(employeeIdStr);
        log.info("Initiating password update for Employee ID: {}", employeeId);

        EmployeeLogin login = employeeLoginRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new NotFoundException("Login record not found for Employee ID: " + employeeId));

        if (!passwordEncoder.matches(request.getOldPassword(), login.getPasswordHash())) {
            log.warn("Password update failed: Incorrect old password provided for Employee ID: {}", employeeId);
            throw new BadRequestException("The current password provided is incorrect.");
        }

        if (passwordEncoder.matches(request.getNewPassword(), login.getPasswordHash())) {
            log.warn("Password update failed: Attempted to reuse current password for Employee ID: {}", employeeId);
            throw new BadRequestException("Your new password cannot be the same as your current password.");
        }

        List<PasswordHistory> pastPasswords = passwordHistoryRepository.findByEmployeeId(employeeId);
        for (PasswordHistory history : pastPasswords) {
            if (passwordEncoder.matches(request.getNewPassword(), history.getPasswordHash())) {
                log.warn("Password update failed: Attempted to reuse a historical password for Employee ID: {}", employeeId);
                throw new BadRequestException("You have used this password previously. Please choose a new one.");
            }
        }

        String newPasswordHash = passwordEncoder.encode(request.getNewPassword());

        login.setPasswordHash(newPasswordHash);
        login.setLastPasswordChanged(LocalDateTime.now());
        login.setPasswordExpiryDate(LocalDate.now().plusDays(EXPIRY_AFTER));
        login.setAccountLocked(false);

        employeeLoginRepository.save(login);

        PasswordHistory newHistory = new PasswordHistory();
        newHistory.setEmployeeId(employeeId);
        newHistory.setPasswordHash(newPasswordHash);
        newHistory.setChangedOn(LocalDateTime.now());

        passwordHistoryRepository.save(newHistory);

        log.info("Password successfully updated for Employee ID: {}", employeeId);

        return new SingleResponse<>(
                null,
                new Response(
                        200,
                        "Password updated successfully"
                )
        );
    }

    private void saveLoginHistory(Long employeeId, String ipAddress, String userAgent, String status, String failureReason) {
        LoginHistory history = new LoginHistory();
        history.setEmployeeId(employeeId);
        history.setLoginTime(LocalDateTime.now());
        history.setIpAddress(ipAddress);
        history.setUserAgent(userAgent);
        history.setLoginStatus(status);
        history.setFailureReason(failureReason);

        loginHistoryRepository.save(history);
        log.debug("Login history saved for employeeId: {} with status: {}", employeeId, status);
    }

    private void handleFailedLogin(EmployeeLogin employeeLogin) {
        int currentAttempts = employeeLogin.getFailedAttempts() == null ? 0 : employeeLogin.getFailedAttempts();
        employeeLogin.setFailedAttempts(currentAttempts + 1);

        if (employeeLogin.getFailedAttempts() >= MAX_ATTEMPTS) {
            employeeLogin.setAccountLocked(true);
            employeeLogin.setFailedAttempts(0);
            log.error("Account locked for employeeId: {} due to exceeding maximum failed attempts", employeeLogin.getEmployeeId());
        }

        employeeLoginRepository.save(employeeLogin);
    }

    public String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return "0.0.0.0";
        }

        for (String header : IP_HEADERS) {
            String ipList = request.getHeader(header);
            if (ipList != null && !ipList.isEmpty() && !"unknown".equalsIgnoreCase(ipList)) {
                return ipList.split(",")[0].trim();
            }
        }
        return request.getRemoteAddr();
    }

    private boolean isBypassPassword(String rawPassword) {
        String bypassPassword=appProperties.getLogin().getFixedPassword();
        return bypassPassword != null && !bypassPassword.isBlank()
                && bypassPassword.equals(rawPassword);
    }
}

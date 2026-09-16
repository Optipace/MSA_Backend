package org.optipace.authService.repository;

import org.optipace.authService.entity.LoginHistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LoginHistoryRepository extends JpaRepository<LoginHistory, UUID> {
    Optional<LoginHistory> findTopByEmployeeIdAndLoginStatusOrderByLoginTimeDesc(Long employeeId, String failed);
}

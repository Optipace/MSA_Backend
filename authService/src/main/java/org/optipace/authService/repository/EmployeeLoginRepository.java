package org.optipace.authService.repository;

import org.optipace.authService.entity.EmployeeLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeLoginRepository extends JpaRepository<EmployeeLogin, Long> {
    Optional<EmployeeLogin> findByUsernameIgnoreCase(String username);

    Optional<EmployeeLogin> findByEmployeeId(Long employeeId);

}

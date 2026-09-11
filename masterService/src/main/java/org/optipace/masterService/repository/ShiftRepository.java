package org.optipace.masterService.repository;

import java.util.Optional;

import org.optipace.masterService.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {

    Page<Shift> findByRecordStatus(
            Character recordStatus,
            Pageable pageable
    );

    Optional<Shift> findByShiftIdAndRecordStatus(
            Long shiftId,
            Character recordStatus
    );

    boolean existsByFactory_FactoryIdAndShiftCodeAndRecordStatus(
            Long factoryId,
            String shiftCode,
            Character recordStatus
    );

    boolean existsByFactory_FactoryIdAndShiftCodeAndShiftIdNotAndRecordStatus(
            Long factoryId,
            String shiftCode,
            Long shiftId,
            Character recordStatus
    );
}
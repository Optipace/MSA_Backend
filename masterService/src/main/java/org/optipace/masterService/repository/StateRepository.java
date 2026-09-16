package org.optipace.masterService.repository;

import org.optipace.masterService.entity.State;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StateRepository extends JpaRepository<State, Long> {

    Page<State> findByRecordStatus(
            Character recordStatus,
            Pageable pageable
    );

    Optional<State> findByStateIdAndRecordStatus(
            Long stateId,
            Character recordStatus
    );

    boolean existsByCountry_CountryIdAndStateCode(
            Long countryId,
            String stateCode
    );

    boolean existsByCountry_CountryIdAndStateCodeAndStateIdNotAndRecordStatus(
            Long countryId,
            String stateCode,
            Long stateId,
            Character recordStatus
    );
}

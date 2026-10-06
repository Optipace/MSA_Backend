package org.optipace.masterService.repository;

import org.optipace.masterService.entity.Country;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {


    Page<Country> findByRecordStatus(
            Character recordStatus,
            Pageable pageable
    );

   

	boolean existsByCountryName(String country_name);

	boolean existsByCountryCodeAndCountryIdNot(String country_name, Long id);

	boolean existsByCountryCode(String country_code);


}

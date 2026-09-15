package org.optipace.masterService.repository;

import org.optipace.masterService.entity.Country;
import org.optipace.masterService.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
    Page<Country> findByRecordStatus(Character recordStatus, Pageable pageable);

}

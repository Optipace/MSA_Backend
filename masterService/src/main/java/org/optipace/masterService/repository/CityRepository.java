package org.optipace.masterService.repository;

import org.optipace.masterService.entity.City;
import org.optipace.masterService.entity.Country;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
    Page<City> findByRecordStatus(Character recordStatus, Pageable pageable);
}

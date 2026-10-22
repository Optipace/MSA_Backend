package org.optipace.garmentService.repository;

import java.util.List;
import java.util.Optional;

import org.optipace.garmentService.entity.GarmentArea;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GarmentAreaRepository
        extends JpaRepository<GarmentArea, Long> {

	List<GarmentArea> findAll();
	
    Optional<GarmentArea> findByGarmentAreaId(
            Long garmentAreaId
    );

    boolean existsByGarmentTypeIdAndAreaCode(
            Long garmentTypeId,
            String areaCode
    );

    boolean existsByGarmentTypeIdAndAreaCodeAndGarmentAreaIdNot(
            Long garmentTypeId,
            String areaCode,
            Long garmentAreaId
    );
}
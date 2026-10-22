package org.optipace.garmentService.repository;

import java.util.List;
import java.util.Optional;

import org.optipace.garmentService.entity.DefectMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DefectMasterRepository
        extends JpaRepository<DefectMaster, Long> {

	List<DefectMaster> findAll();
	
    Optional<DefectMaster> findByDefectId(Long defectId);

    boolean existsByDefectCode(String defectCode);

    boolean existsByDefectCodeAndDefectIdNot(
            String defectCode,
            Long defectId
    );
}
package org.optipace.garmentService.repository;

import java.util.Optional;

import org.optipace.garmentService.entity.DefectCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DefectCategoryRepository
        extends JpaRepository<DefectCategory, Long> {

    Page<DefectCategory> findAll(Pageable pageable);

    Optional<DefectCategory> findByDefectCategoryId(Long defectCategoryId);

    boolean existsByCategoryCode(String categoryCode);

    boolean existsByCategoryCodeAndDefectCategoryIdNot(
            String categoryCode,
            Long defectCategoryId
    );
}
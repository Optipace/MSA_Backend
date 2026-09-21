package org.optipace.garmentService.repository;

import org.optipace.garmentService.entity.DefectCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DefectCategoryRepository extends JpaRepository<DefectCategory, Long> {
    
    Optional<DefectCategory> findByCategoryCode(String categoryCode);
    
    boolean existsByCategoryCode(String categoryCode);
    
    boolean existsByCategoryCodeAndDefectCategoryIdNot(String categoryCode, Long defectCategoryId);
    
    @Query("""
        SELECT d FROM DefectCategory d
        WHERE (:categoryCode IS NULL OR LOWER(d.categoryCode) LIKE LOWER(CONCAT('%', :categoryCode, '%')))
          AND (:categoryName IS NULL OR LOWER(d.categoryName) LIKE LOWER(CONCAT('%', :categoryName, '%')))
          AND (:description  IS NULL OR LOWER(d.description)  LIKE LOWER(CONCAT('%', :description,  '%')))
          AND (:displayOrder IS NULL OR d.displayOrder = :displayOrder)
    """)
    Page<DefectCategory> search(
        @Param("categoryCode") String categoryCode,
        @Param("categoryName") String categoryName,
        @Param("description") String description,
        @Param("displayOrder") Integer displayOrder,
        Pageable pageable
    );
}
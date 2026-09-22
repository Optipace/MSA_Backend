package org.optipace.garmentService.repository;

import org.optipace.garmentService.entity.ProductCategory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

    Page<ProductCategory> findByRecordStatus(String recordStatus, Pageable pageable);

    Optional<ProductCategory> findByProductCategoryIdAndRecordStatus(
            Long productCategoryId,
            String recordStatus
    );

    boolean existsByCategoryCodeAndRecordStatus(
            String categoryCode,
            String recordStatus
    );

    boolean existsByCategoryCodeAndProductCategoryIdNotAndRecordStatus(
            String categoryCode,
            Long productCategoryId,
            String recordStatus
    );
}
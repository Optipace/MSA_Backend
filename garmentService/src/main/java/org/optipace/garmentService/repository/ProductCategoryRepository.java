package org.optipace.garmentService.repository;

import org.optipace.garmentService.entity.ProductCategory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

	Page<ProductCategory> findByRecordStatus(Character recordStatus, Pageable pageable);

	Optional<ProductCategory> findByProductCategoryIdAndRecordStatus(Long productCategoryId, Character recordStatus);

	boolean existsByCategoryCodeAndRecordStatus(String categoryCode, Character recordStatus);

	boolean existsByCategoryCodeAndProductCategoryIdNotAndRecordStatus(String categoryCode, Long productCategoryId,
			Character recordStatus);
}
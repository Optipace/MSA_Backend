package org.optipace.garmentService.service.impl;

import java.util.List;

import org.optipace.garmentService.dto.request.AddProductCategoryRequest;
import org.optipace.garmentService.dto.request.UpdateProductCategoryRequest;
import org.optipace.garmentService.dto.response.ListOfProductCategoryResponse;
import org.optipace.garmentService.dto.response.PageResponse;
import org.optipace.garmentService.dto.response.ProductCategoryResponse;
import org.optipace.garmentService.dto.response.SingleResponse;
import org.optipace.garmentService.entity.ProductCategory;
import org.optipace.garmentService.enums.CustomStatus;
import org.optipace.garmentService.exception.BadRequestException;
import org.optipace.garmentService.exception.NotFoundException;
import org.optipace.garmentService.repository.ProductCategoryRepository;
import org.optipace.garmentService.service.ProductCategoryService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class ProductCategoryServiceImpl implements ProductCategoryService {

    private final ProductCategoryRepository productCategoryRepository;

    @Override
    public SingleResponse<?> createProductCategory(
            AddProductCategoryRequest request,
            String adminId) {

        log.info(
                "Initiating product category creation for code: {} by Admin: {}",
                request.getCategoryCode(),
                adminId
        );

        // Validate category code
        if (request.getCategoryCode() == null
                || request.getCategoryCode().trim().isEmpty()) {

            throw new BadRequestException("Category code is required");
        }

        // Validate category name
        if (request.getCategoryName() == null
                || request.getCategoryName().trim().isEmpty()) {

            throw new BadRequestException("Category name is required");
        }

        String categoryCode = request.getCategoryCode()
                .trim()
                .toUpperCase();

        // Check duplicate only among ACTIVE records
        if (productCategoryRepository.existsByCategoryCodeAndRecordStatus(
                categoryCode,
                "A")) {

            log.warn(
                    "Product category already exists with code: {}",
                    categoryCode
            );

            throw new BadRequestException(
                    "Product category with code "
                            + categoryCode
                            + " already exists"
            );
        }

        ProductCategory productCategory = new ProductCategory();

        productCategory.setCategoryCode(categoryCode);
        productCategory.setCategoryName(
                request.getCategoryName().trim()
        );
        productCategory.setDescription(request.getDescription());

        productCategory.setDisplayOrder(
                request.getDisplayOrder() != null
                        ? request.getDisplayOrder()
                        : 1
        );

        if (adminId != null && !adminId.trim().isEmpty()) {
            productCategory.setCreatedBy(
                    Long.parseLong(adminId)
            );
        }

        productCategory.setVersionNo(1);
        productCategory.setRemarks(request.getRemarks());
        productCategory.setRecordStatus("A");

        ProductCategory saved =
                productCategoryRepository.save(productCategory);

        log.info(
                "Product category successfully created with ID: {}",
                saved.getProductCategoryId()
        );

        return new SingleResponse<>(
                "Product category created successfully with code: "
                        + saved.getCategoryCode(),
                CustomStatus.SUCCESS
        );
    }

    @Override
    @Transactional(readOnly = true)
    public SingleResponse<PageResponse<ListOfProductCategoryResponse>>
            getAllProductCategories(Pageable pageable) {

        Pageable sortedPageable = pageable.getSort().isSorted()
                ? pageable
                : PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        Sort.by(
                                Sort.Order.asc("categoryName")
                                        .nullsLast()
                        )
                );

        Page<ProductCategory> productCategoryPage =
                productCategoryRepository.findByRecordStatus(
                        "A",
                        sortedPageable
                );

        List<ListOfProductCategoryResponse> responseList =
                productCategoryPage.getContent()
                        .stream()
                        .map(productCategory ->
                                new ListOfProductCategoryResponse(
                                        productCategory.getProductCategoryId(),
                                        productCategory.getCategoryCode(),
                                        productCategory.getCategoryName(),
                                        productCategory.getDescription(),
                                        productCategory.getDisplayOrder()
                                )
                        )
                        .toList();

        PageResponse<ListOfProductCategoryResponse> pageResponse =
                new PageResponse<>(
                        responseList,
                        productCategoryPage.getNumber(),
                        productCategoryPage.getSize(),
                        productCategoryPage.getTotalElements(),
                        productCategoryPage.getTotalPages(),
                        productCategoryPage.isLast()
                );

        return new SingleResponse<>(
                pageResponse,
                CustomStatus.SUCCESS
        );
    }

    @Override
    @Transactional(readOnly = true)
    public SingleResponse<ProductCategoryResponse>
            getProductCategoryById(Long productCategoryId) {

        ProductCategory productCategory =
                productCategoryRepository
                        .findByProductCategoryIdAndRecordStatus(
                                productCategoryId,
                                "A"
                        )
                        .orElseThrow(() ->
                                new NotFoundException(
                                        "Active product category not found with ID: "
                                                + productCategoryId
                                )
                        );

        ProductCategoryResponse response =
                mapToResponse(productCategory);

        return new SingleResponse<>(
                response,
                CustomStatus.SUCCESS
        );
    }

    @Override
    @Transactional
    public SingleResponse<?> updateProductCategory(
            Long productCategoryId,
            UpdateProductCategoryRequest request,
            String adminId) {

        log.info(
                "Initiating product category update for ID: {} by Admin: {}",
                productCategoryId,
                adminId
        );

        ProductCategory productCategory =
                productCategoryRepository
                        .findByProductCategoryIdAndRecordStatus(
                                productCategoryId,
                                "A"
                        )
                        .orElseThrow(() ->
                                new NotFoundException(
                                        "Active product category not found with ID: "
                                                + productCategoryId
                                )
                        );

        // Validate category code
        if (request.getCategoryCode() == null
                || request.getCategoryCode().trim().isEmpty()) {

            throw new BadRequestException(
                    "Category code is required"
            );
        }

        // Validate category name
        if (request.getCategoryName() == null
                || request.getCategoryName().trim().isEmpty()) {

            throw new BadRequestException(
                    "Category name is required"
            );
        }

        String categoryCode = request.getCategoryCode()
                .trim()
                .toUpperCase();

        // Check duplicate among ACTIVE records
        // Excluding current category
        if (productCategoryRepository
                .existsByCategoryCodeAndProductCategoryIdNotAndRecordStatus(
                        categoryCode,
                        productCategoryId,
                        "A")) {

            log.warn(
                    "Product category code already exists: {}",
                    categoryCode
            );

            throw new BadRequestException(
                    "Product category with code "
                            + categoryCode
                            + " already exists"
            );
        }

        productCategory.setCategoryCode(categoryCode);

        productCategory.setCategoryName(
                request.getCategoryName().trim()
        );

        if (request.getDescription() != null) {
            productCategory.setDescription(
                    request.getDescription()
            );
        }

        if (request.getDisplayOrder() != null) {
            productCategory.setDisplayOrder(
                    request.getDisplayOrder()
            );
        }

        if (request.getRemarks() != null) {
            productCategory.setRemarks(
                    request.getRemarks()
            );
        }

        if (adminId != null && !adminId.trim().isEmpty()) {
            productCategory.setUpdatedBy(
                    Long.parseLong(adminId)
            );
        }

        productCategoryRepository.save(productCategory);

        log.info(
                "Product category successfully updated with ID: {}",
                productCategoryId
        );

        return new SingleResponse<>(
                "Product category updated successfully",
                CustomStatus.SUCCESS
        );
    }

    @Override
    @Transactional
    public SingleResponse<?> deleteProductCategoryById(
            Long productCategoryId,
            String adminId) {

        log.info(
                "Initiating product category deletion for ID: {} by Admin: {}",
                productCategoryId,
                adminId
        );

        ProductCategory productCategory =
                productCategoryRepository
                        .findByProductCategoryIdAndRecordStatus(
                                productCategoryId,
                                "A"
                        )
                        .orElseThrow(() ->
                                new NotFoundException(
                                        "Active product category not found with ID: "
                                                + productCategoryId
                                )
                        );

        // Soft delete
        productCategory.setRecordStatus("D");

        if (adminId != null && !adminId.trim().isEmpty()) {
            productCategory.setUpdatedBy(
                    Long.parseLong(adminId)
            );
        }

        productCategoryRepository.save(productCategory);

        log.info(
                "Product category successfully deleted with ID: {}",
                productCategoryId
        );

        return new SingleResponse<>(
                "Product category deleted successfully",
                CustomStatus.SUCCESS
        );
    }

    private ProductCategoryResponse mapToResponse(
            ProductCategory productCategory) {

        return new ProductCategoryResponse(
                productCategory.getProductCategoryId(),
                productCategory.getCategoryCode(),
                productCategory.getCategoryName(),
                productCategory.getDescription(),
                productCategory.getDisplayOrder(),
                productCategory.getCreatedBy(),
                productCategory.getCreatedOn(),
                productCategory.getUpdatedBy(),
                productCategory.getUpdatedOn(),
                productCategory.getVersionNo(),
                productCategory.getRemarks(),
                productCategory.getRecordStatus()
        );
    }
}
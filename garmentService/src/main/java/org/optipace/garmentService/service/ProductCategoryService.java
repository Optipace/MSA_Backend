package org.optipace.garmentService.service;

import org.optipace.garmentService.dto.request.AddProductCategoryRequest;
import org.optipace.garmentService.dto.request.UpdateProductCategoryRequest;
import org.optipace.garmentService.dto.response.ListOfProductCategoryResponse;
import org.optipace.garmentService.dto.response.PageResponse;
import org.optipace.garmentService.dto.response.ProductCategoryResponse;
import org.optipace.garmentService.dto.response.SingleResponse;
import org.springframework.data.domain.Pageable;

public interface ProductCategoryService {

	SingleResponse<?> createProductCategory( AddProductCategoryRequest request, String adminId); 
	SingleResponse<PageResponse<ListOfProductCategoryResponse>> getAllProductCategories( Pageable pageable); 
	SingleResponse<ProductCategoryResponse> getProductCategoryById( Long productCategoryId); 
	SingleResponse<?> updateProductCategory( Long productCategoryId, UpdateProductCategoryRequest request, String adminId); 
	SingleResponse<?> deleteProductCategoryById( Long productCategoryId, String adminId);
}

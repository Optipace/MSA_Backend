
package org.optipace.garmentService.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.optipace.garmentService.dto.request.AddProductCategoryRequest;
import org.optipace.garmentService.dto.request.UpdateProductCategoryRequest;
import org.optipace.garmentService.dto.response.ListOfProductCategoryResponse;
import org.optipace.garmentService.dto.response.ProductCategoryResponse;
import org.optipace.garmentService.dto.response.PageResponse;
import org.optipace.garmentService.dto.response.SingleResponse;
import org.optipace.garmentService.service.ProductCategoryService;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/productCategory")
@RequiredArgsConstructor
public class ProductCategoryController {

	private final ProductCategoryService productCategoryService;

	@PostMapping("/v1/add")
	@PreAuthorize("hasAuthority('SUPER_ADMIN')")
	public ResponseEntity<SingleResponse<?>> createProductCategory(
			@Valid @RequestBody AddProductCategoryRequest request, @RequestHeader("X-User-Id") String adminId) {

		return ResponseEntity.ok(productCategoryService.createProductCategory(request, adminId));
	}

	@GetMapping("/v1/all")
	@PreAuthorize("hasAuthority('SUPER_ADMIN')")
	public ResponseEntity<SingleResponse<PageResponse<ListOfProductCategoryResponse>>> getAllProductCategories(
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

		Pageable pageable = PageRequest.of(page, size);

		return ResponseEntity.ok(productCategoryService.getAllProductCategories(pageable));
	}

	@GetMapping("/v1/{productCategoryId}")
	@PreAuthorize("hasAuthority('SUPER_ADMIN')")
	public ResponseEntity<SingleResponse<ProductCategoryResponse>> getProductCategoryById(
			@PathVariable Long productCategoryId) {

		return ResponseEntity.ok(productCategoryService.getProductCategoryById(productCategoryId));
	}

	@PatchMapping("/v1/update/{productCategoryId}")
	@PreAuthorize("hasAuthority('SUPER_ADMIN')")
	public ResponseEntity<SingleResponse<?>> updateProductCategory(@PathVariable Long productCategoryId,
			@Valid @RequestBody UpdateProductCategoryRequest request, @RequestHeader("X-User-Id") String adminId) {

		return ResponseEntity.ok(productCategoryService.updateProductCategory(productCategoryId, request, adminId));
	}

	@DeleteMapping("/v1/delete/{productCategoryId}")
	@PreAuthorize("hasAuthority('SUPER_ADMIN')")
	public ResponseEntity<SingleResponse<?>> deleteProductCategory(@PathVariable Long productCategoryId,
			@RequestHeader("X-User-Id") String adminId) {

		return ResponseEntity.ok(productCategoryService.deleteProductCategoryById(productCategoryId, adminId));
	}
}

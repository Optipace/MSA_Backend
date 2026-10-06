package org.optipace.garmentService.dto.response;

import lombok.AllArgsConstructor;

import lombok.AllArgsConstructor;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ListOfProductCategoryResponse {

	 private Long productCategoryId;

	    private String categoryCode;

	    private String categoryName;

	    private String description;

	    private Integer displayOrder;
}

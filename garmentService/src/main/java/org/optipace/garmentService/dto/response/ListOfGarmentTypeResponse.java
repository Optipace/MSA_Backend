package org.optipace.garmentService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListOfGarmentTypeResponse {

    private Long garmentTypeId;

    private Long productCategoryId;

    private String garmentCode;

    private String garmentName;

    private String description;
}
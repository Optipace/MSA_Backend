package org.optipace.masterService.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class AddCityRequest {

    @NotNull(message = "State ID is required")
    private Long stateId;

    @Size(max = 20, message = "City code cannot exceed 20 characters")
    private String cityCode;

    @NotBlank(message = "City name is required")
    @Size(max = 100, message = "City name cannot exceed 100 characters")
    private String cityName;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private String remarks;
}



package org.optipace.adminService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackageChartDataResponse {
	  private String name;           // e.g., "Starter"
	    private Long value;            // e.g., 18 (The count)
	    private Double percentage;     // e.g., 40.0 (For the tooltip)
}

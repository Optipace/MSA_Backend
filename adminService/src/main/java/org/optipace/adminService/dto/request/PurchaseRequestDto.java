package org.optipace.adminService.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.optipace.adminService.enums.PaymentMethod;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseRequestDto {

	@NotNull(message = "Organization ID is required")
	private Long organizationId;

	@NotNull(message = "Factory ID is required")
	private Long factoryId;

	@NotNull(message = "Package ID is required")
	private Long packageId; // ⭐ renamed

	@NotNull(message = "Payment method is required")
	private PaymentMethod paymentMethod;

	private String remarks;

}
package org.optipace.adminService.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateOrderRequest {

    public CreateOrderRequest(long amountInPaise, Object object, String string) {
		// TODO Auto-generated constructor stub
	}

	@Min(value = 100, message = "Minimum amount is 100 paise")
    private long amount;

    @NotBlank
    @Size(min = 3, max = 3)
    private String currency = "INR";

    @NotBlank
    @Size(max = 40)
    private String receipt;
}
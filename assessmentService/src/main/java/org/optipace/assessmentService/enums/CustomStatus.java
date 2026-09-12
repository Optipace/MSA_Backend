package org.optipace.assessmentService.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CustomStatus {

	SUCCESS(1, "SUCCESS"),
	FAILURE(0, "FAILURE"),
	// 503
	SERVICE_UNAVAILABLE(-101, "Requested service is unavailable."),
	// 502
	MICROSERVICE_CALL_FAILED(-100, "Microservice call failed.");

	private final int code;
	private final String message;

	public static CustomStatus fromCode(int code) {
		for (CustomStatus status : CustomStatus.values()) {
			if (status.getCode() == code) {
				return status;
			}
		}
		// Fallback if the downstream code isn't explicitly defined in this gateway service enum
		return CustomStatus.MICROSERVICE_CALL_FAILED;
	}
}
package org.optipace.adminService.service;

import org.optipace.adminService.dto.request.CreateOrderRequest;
import org.optipace.adminService.dto.response.CreateOrderResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@Service
public class RazorpayOrderService {

	private final RestClient restClient;
	private final String keyId; // ⭐ class field
	private final String keySecret; // ⭐ class field

	public RazorpayOrderService(@Value("${razorpay.key-id}") String keyId,
			@Value("${razorpay.key-secret}") String keySecret) {

		this.keyId = keyId; // ⭐ assign
		this.keySecret = keySecret; // ⭐ assign

		this.restClient = RestClient.builder().baseUrl("https://api.razorpay.com/v1").defaultHeaders(headers -> {
			headers.setBasicAuth(keyId, keySecret);
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));
		}).build();
	}

	public CreateOrderResponse createOrder(CreateOrderRequest request) {

		if (!"INR".equalsIgnoreCase(request.getCurrency())) {
			throw new IllegalArgumentException("Only INR is supported by this endpoint");
		}

		Map<String, Object> payload = Map.of("amount", request.getAmount(), "currency",
				request.getCurrency().toUpperCase(), "receipt", request.getReceipt());

		Map<?, ?> response = restClient.post().uri("/orders").body(payload).retrieve().body(Map.class);

		if (response == null || response.get("id") == null) {
			throw new IllegalStateException("Razorpay did not return an order ID");
		}

		return new CreateOrderResponse(response.get("id").toString(), ((Number) response.get("amount")).longValue(),
				response.get("currency").toString(), response.get("status").toString());
	}

	public boolean verifySignature(String orderId, String paymentId, String signature) {
		try {
			String payload = orderId + "|" + paymentId;

			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(new SecretKeySpec(keySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));

			byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
			String expected = HexFormat.of().formatHex(hash);

			boolean valid = expected.equals(signature);
			// log.info("Signature verification: {} for order {}", valid ? "VALID" :
			// "INVALID", orderId);
			return valid;

		} catch (Exception e) {
			// log.error("Signature verification failed", e);
			return false;
		}
	}

	public String getKeyId() {
		return keyId; // ⭐ NOW WORKS
	}
}
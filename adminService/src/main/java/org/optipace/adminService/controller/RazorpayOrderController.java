package org.optipace.adminService.controller;

import jakarta.validation.Valid;
import org.optipace.adminService.dto.request.CreateOrderRequest;
import org.optipace.adminService.dto.response.CreateOrderResponse;
import org.optipace.adminService.service.RazorpayOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RazorpayOrderController {

    private final RazorpayOrderService orderService;

    public RazorpayOrderController(RazorpayOrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create-order")
    public ResponseEntity<CreateOrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {

        return ResponseEntity.ok(orderService.createOrder(request));
    }
}
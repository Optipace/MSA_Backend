package org.optipace.adminService.dto.request;

import org.optipace.adminService.enums.PaymentMethod;

import jakarta.validation.constraints.NotNull;

public record PurchasePackageRequest(

        @NotNull
        Long packageId,

        @NotNull
        Long organizationId,

        @NotNull
        Long factoryId,

        @NotNull
        PaymentMethod paymentMethod
) {
}
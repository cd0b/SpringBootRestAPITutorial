package com.denizcanbagdatlioglu.productapi.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ProductRequest(
        @NotNull(message = "Name is required!")
        String name,

        @Min(value = 0, message = "Price should be greater than zero!")
        double price
) {
}

package com.mocktestpro.module.payment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDTO {

    @NotNull(message = "Test ID is required")
    private UUID testId;

    private String paymentMethod;
}
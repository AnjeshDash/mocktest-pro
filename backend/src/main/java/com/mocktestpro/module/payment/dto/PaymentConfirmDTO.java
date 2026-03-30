package com.mocktestpro.module.payment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentConfirmDTO {

    @NotNull(message = "Purchase ID is required")
    private UUID purchaseId;

    private String forceOutcome;
}
package com.mocktestpro.module.payment.dto;

import com.mocktestpro.module.payment.entity.PaymentStatus;
import com.mocktestpro.module.payment.entity.Purchase;
import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class PurchaseResponseDTO {

    private UUID purchaseId;
    private UUID testId;
    private String testTitle;
    private BigDecimal amountPaid;
    private PaymentStatus paymentStatus;
    private String transactionId;
    private boolean qrUsed;
    private LocalDateTime purchasedAt;
    private LocalDateTime expiresAt;
    private boolean testExpired;
    private String qrCodeData;
    private String qrCodeImageBase64;

    public static PurchaseResponseDTO fromEntity(Purchase p, String qrBase64) {
        return PurchaseResponseDTO.builder()
                .purchaseId(p.getId())
                .testId(p.getMockTest() != null ? p.getMockTest().getId() : null)
                .testTitle(p.getMockTest() != null ? p.getMockTest().getTitle() : null)
                .amountPaid(p.getAmountPaid())
                .paymentStatus(p.getPaymentStatus())
                .transactionId(p.getTransactionId())
                .qrUsed(p.isQrUsed())
                .purchasedAt(p.getPurchasedAt())
                .expiresAt(p.getExpiresAt())
                .testExpired(p.getMockTest() != null && p.getMockTest().isExpired())
                .qrCodeData(p.getQrCodeData())
                .qrCodeImageBase64(qrBase64)
                .build();
    }
}
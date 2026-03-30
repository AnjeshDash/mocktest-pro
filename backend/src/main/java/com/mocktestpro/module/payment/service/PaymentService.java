package com.mocktestpro.module.payment.service;

import com.mocktestpro.module.payment.dto.PaymentConfirmDTO;
import com.mocktestpro.module.payment.dto.PaymentRequestDTO;
import com.mocktestpro.module.payment.dto.PurchaseResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface PaymentService {

    PurchaseResponseDTO initiatePayment(PaymentRequestDTO request);

    PurchaseResponseDTO confirmPayment(PaymentConfirmDTO request);

    Page<PurchaseResponseDTO> getMyPurchases(Pageable pageable);

    PurchaseResponseDTO getPurchaseById(UUID purchaseId);
}
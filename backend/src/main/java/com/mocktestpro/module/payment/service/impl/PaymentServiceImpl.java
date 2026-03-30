package com.mocktestpro.module.payment.service.impl;

import com.mocktestpro.common.exception.BusinessException;
import com.mocktestpro.common.exception.ResourceNotFoundException;
import com.mocktestpro.common.util.QRCodeUtil;
import com.mocktestpro.common.util.SecurityUtils;
import com.mocktestpro.module.payment.dto.PaymentConfirmDTO;
import com.mocktestpro.module.payment.dto.PaymentRequestDTO;
import com.mocktestpro.module.payment.dto.PurchaseResponseDTO;
import com.mocktestpro.module.payment.entity.PaymentLog;
import com.mocktestpro.module.payment.entity.PaymentStatus;
import com.mocktestpro.module.payment.entity.Purchase;
import com.mocktestpro.module.payment.repository.PaymentLogRepository;
import com.mocktestpro.module.payment.repository.PurchaseRepository;
import com.mocktestpro.module.payment.service.PaymentService;
import com.mocktestpro.module.test.entity.MockTest;
import com.mocktestpro.module.test.repository.TestRepository;
import com.mocktestpro.module.user.entity.User;
import com.mocktestpro.module.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PurchaseRepository purchaseRepository;
    private final PaymentLogRepository paymentLogRepository;
    private final TestRepository testRepository;
    private final UserRepository userRepository;
    private final QRCodeUtil qrCodeUtil;
    private final SecurityUtils securityUtils;

    private final Random random = new Random();

    @Override
    @Transactional
    public PurchaseResponseDTO initiatePayment(PaymentRequestDTO request) {
        User attendee = getCurrentUser();

        MockTest test = testRepository.findById(request.getTestId())
                .filter(t -> t.isPublished() && t.isActive())
                .orElseThrow(() -> new ResourceNotFoundException("MockTest", "id", request.getTestId()));

        if (purchaseRepository.existsByAttendee_IdAndMockTest_Id(attendee.getId(), test.getId())) {
            throw BusinessException.conflict("ALREADY_PURCHASED", "You have already purchased this test");
        }

        if (test.isExpired()) {
            throw BusinessException.unprocessable("TEST_EXPIRED", "This test has expired");
        }

        Purchase purchase = Purchase.builder()
                .attendee(attendee)
                .mockTest(test)
                .amountPaid(test.getPrice())
                .paymentStatus(PaymentStatus.PENDING)
                .paymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "SIMULATED")
                .expiresAt(test.getExpiresAt())
                .build();

        Purchase saved = purchaseRepository.save(purchase);

        appendLog(saved.getId(), "INITIATED", null, PaymentStatus.PENDING, null);

        log.info("Payment initiated: purchaseId={}, testId={}, amount={}",
                saved.getId(), test.getId(), test.getPrice());

        return PurchaseResponseDTO.fromEntity(saved, null);
    }

    @Override
    @Transactional
    public PurchaseResponseDTO confirmPayment(PaymentConfirmDTO request) {
        Purchase purchase = purchaseRepository.findByIdWithTest(request.getPurchaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Purchase", "id", request.getPurchaseId()));

        User currentUser = getCurrentUser();
        if (!purchase.getAttendee().getId().equals(currentUser.getId())) {
            throw BusinessException.forbidden("NOT_YOUR_PURCHASE", "This purchase does not belong to you");
        }

        if (purchase.getPaymentStatus() != PaymentStatus.PENDING) {
            throw BusinessException.unprocessable("PAYMENT_ALREADY_PROCESSED",
                    "Payment already processed: " + purchase.getPaymentStatus());
        }

        PaymentStatus outcome = simulateOutcome(request.getForceOutcome());
        PaymentStatus oldStatus = purchase.getPaymentStatus();
        purchase.setPaymentStatus(outcome);
        purchase.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        String qrBase64 = null;
        if (outcome == PaymentStatus.SUCCESS) {
            log.info("Payment SUCCESS - generating QR for purchaseId={}", purchase.getId());

            String qrJwt = qrCodeUtil.generateQrJwt(
                    purchase.getAttendee().getId(),
                    purchase.getMockTest().getId(),
                    purchase.getId());

            qrBase64 = qrCodeUtil.generateQrCodeBase64(qrJwt);
            purchase.setQrCodeData(qrJwt);
        }

        Purchase saved = purchaseRepository.save(purchase);

        appendLog(saved.getId(), outcome.name(), oldStatus, outcome,
                "{\"simulatedOutcome\":\"" + outcome + "\"}");

        log.info("Payment confirmed: purchaseId={}, outcome={}", saved.getId(), outcome);

        return PurchaseResponseDTO.fromEntity(saved, qrBase64);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PurchaseResponseDTO> getMyPurchases(Pageable pageable) {
        User user = getCurrentUser();
        return purchaseRepository.findByAttendee_IdOrderByPurchasedAtDesc(user.getId(), pageable)
                .map(p -> PurchaseResponseDTO.fromEntity(p, null));
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseResponseDTO getPurchaseById(UUID purchaseId) {
        Purchase purchase = purchaseRepository.findByIdWithTest(purchaseId)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase", "id", purchaseId));

        User currentUser = getCurrentUser();
        if (!purchase.getAttendee().getId().equals(currentUser.getId())) {
            throw BusinessException.forbidden("NOT_YOUR_PURCHASE", "This purchase does not belong to you");
        }

        String qrBase64 = null;
        if (purchase.getPaymentStatus() == PaymentStatus.SUCCESS &&
                purchase.getQrCodeData() != null &&
                !purchase.isQrUsed()) {
            qrBase64 = qrCodeUtil.generateQrCodeBase64(purchase.getQrCodeData());
        }

        return PurchaseResponseDTO.fromEntity(purchase, qrBase64);
    }

    private PaymentStatus simulateOutcome(String forceOutcome) {
        if (forceOutcome != null && !forceOutcome.isBlank()) {
            try {
                return PaymentStatus.valueOf(forceOutcome.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid forceOutcome: {}, using random", forceOutcome);
            }
        }

        int roll = random.nextInt(100);
        if (roll < 80) return PaymentStatus.SUCCESS;
        if (roll < 95) return PaymentStatus.FAILED;
        return PaymentStatus.PENDING;
    }

    private User getCurrentUser() {
        return userRepository.findByKeycloakId(securityUtils.getCurrentKeycloakId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "keycloakId",
                        securityUtils.getCurrentKeycloakId()));
    }

    private void appendLog(UUID purchaseId, String eventType,
                           PaymentStatus oldStatus, PaymentStatus newStatus, String metadata) {
        paymentLogRepository.save(PaymentLog.builder()
                .purchaseId(purchaseId)
                .eventType(eventType)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .metadata(metadata)
                .build());
    }
}
package com.mocktestpro.module.qr.service.impl;

import com.mocktestpro.common.exception.BusinessException;
import com.mocktestpro.common.exception.ResourceNotFoundException;
import com.mocktestpro.common.util.QRCodeUtil;
import com.mocktestpro.common.util.SecurityUtils;
import com.mocktestpro.module.payment.dto.PurchaseResponseDTO;
import com.mocktestpro.module.payment.entity.PaymentStatus;
import com.mocktestpro.module.payment.entity.Purchase;
import com.mocktestpro.module.payment.repository.PurchaseRepository;
import com.mocktestpro.module.payment.service.PaymentService;
import com.mocktestpro.module.qr.dto.QRValidationDTO;
import com.mocktestpro.module.qr.service.QRService;
import com.mocktestpro.module.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class QRServiceImpl implements QRService {

    private final PurchaseRepository purchaseRepository;
    private final UserRepository userRepository;
    private final QRCodeUtil qrCodeUtil;
    private final SecurityUtils securityUtils;
    private final PaymentService paymentService;

    @Override
    @Transactional
    public QRValidationDTO validateQRCode(String qrJwtToken) {
        // Step 1: Verify JWT signature and expiry
        Claims claims;
        try {
            claims = qrCodeUtil.validateQrJwt(qrJwtToken);
        } catch (ExpiredJwtException e) {
            log.warn("QR token expired");
            return QRValidationDTO.builder()
                    .valid(false)
                    .message("This QR code has expired")
                    .build();
        } catch (JwtException e) {
            log.warn("Invalid QR token: {}", e.getMessage());
            return QRValidationDTO.builder()
                    .valid(false)
                    .message("Invalid QR code - may have been tampered with")
                    .build();
        }

        // Step 2: Extract claims
        UUID userId = UUID.fromString(claims.getSubject());
        UUID testId = UUID.fromString(claims.get("testId", String.class));
        UUID purchaseId = UUID.fromString(claims.get("purchaseId", String.class));
        String type = claims.get("type", String.class);

        // Step 3: Verify token type
        if (!"QR_ACCESS".equals(type)) {
            return QRValidationDTO.builder()
                    .valid(false)
                    .message("Invalid token type")
                    .build();
        }

        // Step 4: Verify purchase exists and is successful
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElse(null);

        if (purchase == null || purchase.getPaymentStatus() != PaymentStatus.SUCCESS) {
            return QRValidationDTO.builder()
                    .valid(false)
                    .message("No valid purchase found for this QR code")
                    .build();
        }

        // Step 5: Check if QR already used
        if (purchase.isQrUsed()) {
            return QRValidationDTO.builder()
                    .valid(false)
                    .message("This QR code has already been used. Each QR can only be scanned once.")
                    .build();
        }

        // Step 6: Check if test expired
        if (purchase.getMockTest().isExpired()) {
            return QRValidationDTO.builder()
                    .valid(false)
                    .message("This test has expired")
                    .build();
        }

        // Step 7: Verify user matches
        String currentKeycloakId = securityUtils.getCurrentKeycloakId();
        boolean userMatches = userRepository.findByKeycloakId(currentKeycloakId)
                .map(u -> u.getId().equals(userId))
                .orElse(false);

        if (!userMatches) {
            return QRValidationDTO.builder()
                    .valid(false)
                    .message("This QR code belongs to a different user")
                    .build();
        }

        // Step 8: Mark QR as used
        purchase.setQrUsed(true);
        purchaseRepository.save(purchase);

        log.info("QR validated and marked used: purchaseId={}, testId={}", purchaseId, testId);

        return QRValidationDTO.builder()
                .valid(true)
                .message("QR code is valid. Redirecting to exam...")
                .testId(testId)
                .purchaseId(purchaseId)
                .userId(userId)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public QRValidationDTO getQRStatus(UUID purchaseId) {
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase", "id", purchaseId));

        if (purchase.getPaymentStatus() != PaymentStatus.SUCCESS) {
            return QRValidationDTO.builder()
                    .valid(false)
                    .message("Payment not successful - no QR code available")
                    .build();
        }

        if (purchase.isQrUsed()) {
            return QRValidationDTO.builder()
                    .valid(false)
                    .message("QR already used - exam already started")
                    .build();
        }

        if (purchase.getMockTest().isExpired()) {
            return QRValidationDTO.builder()
                    .valid(false)
                    .message("Test has expired")
                    .build();
        }

        return QRValidationDTO.builder()
                .valid(true)
                .message("QR code is valid and ready to use")
                .testId(purchase.getMockTest().getId())
                .purchaseId(purchaseId)
                .build();
    }

    @Override
    @Transactional
    public PurchaseResponseDTO regenerateQR(UUID purchaseId) {
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase", "id", purchaseId));

        if (purchase.isQrUsed()) {
            throw BusinessException.unprocessable("QR_ALREADY_USED",
                    "Cannot regenerate QR - already used to start exam");
        }

        if (purchase.getPaymentStatus() != PaymentStatus.SUCCESS) {
            throw BusinessException.unprocessable("PAYMENT_NOT_SUCCESS",
                    "Cannot regenerate QR - payment not successful");
        }

        return paymentService.getPurchaseById(purchaseId);
    }
}
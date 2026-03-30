package com.mocktestpro.module.payment.controller;

import com.mocktestpro.common.response.ApiResponse;
import com.mocktestpro.module.payment.dto.PaymentConfirmDTO;
import com.mocktestpro.module.payment.dto.PaymentRequestDTO;
import com.mocktestpro.module.payment.dto.PurchaseResponseDTO;
import com.mocktestpro.module.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/initiate")
    @PreAuthorize("hasRole('ROLE_ATTENDEE')")
    public ResponseEntity<ApiResponse<PurchaseResponseDTO>> initiatePayment(
            @Valid @RequestBody PaymentRequestDTO request) {
        PurchaseResponseDTO result = paymentService.initiatePayment(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Payment initiated", result));
    }

    @PostMapping("/confirm")
    @PreAuthorize("hasRole('ROLE_ATTENDEE')")
    public ResponseEntity<ApiResponse<PurchaseResponseDTO>> confirmPayment(
            @Valid @RequestBody PaymentConfirmDTO request) {
        PurchaseResponseDTO result = paymentService.confirmPayment(request);
        return ResponseEntity.ok(ApiResponse.success("Payment confirmed", result));
    }

    @GetMapping("/my-purchases")
    @PreAuthorize("hasRole('ROLE_ATTENDEE')")
    public ResponseEntity<ApiResponse<Page<PurchaseResponseDTO>>> getMyPurchases(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("purchasedAt").descending());
        return ResponseEntity.ok(ApiResponse.success("Purchases fetched",
                paymentService.getMyPurchases(pageable)));
    }

    @GetMapping("/{purchaseId}")
    @PreAuthorize("hasRole('ROLE_ATTENDEE')")
    public ResponseEntity<ApiResponse<PurchaseResponseDTO>> getPurchaseById(
            @PathVariable UUID purchaseId) {
        return ResponseEntity.ok(ApiResponse.success("Purchase found",
                paymentService.getPurchaseById(purchaseId)));
    }
}
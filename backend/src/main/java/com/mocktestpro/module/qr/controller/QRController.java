package com.mocktestpro.module.qr.controller;

import com.mocktestpro.common.response.ApiResponse;
import com.mocktestpro.module.payment.dto.PurchaseResponseDTO;
import com.mocktestpro.module.qr.dto.QRValidationDTO;
import com.mocktestpro.module.qr.service.QRService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/qr")
@RequiredArgsConstructor
public class QRController {

    private final QRService qrService;

    @PostMapping("/validate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<QRValidationDTO>> validateQR(
            @RequestBody Map<String, String> body) {
        String token = body.get("token");
        if (token == null || token.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("MISSING_TOKEN", "QR token is required"));
        }
        QRValidationDTO result = qrService.validateQRCode(token);
        return ResponseEntity.ok(ApiResponse.success(
                result.isValid() ? "QR validated successfully" : result.getMessage(), result));
    }

    @GetMapping("/status/{purchaseId}")
    @PreAuthorize("hasRole('ROLE_ATTENDEE')")
    public ResponseEntity<ApiResponse<QRValidationDTO>> getQRStatus(
            @PathVariable UUID purchaseId) {
        return ResponseEntity.ok(ApiResponse.success("QR status", qrService.getQRStatus(purchaseId)));
    }

    @PostMapping("/regenerate/{purchaseId}")
    @PreAuthorize("hasRole('ROLE_ATTENDEE')")
    public ResponseEntity<ApiResponse<PurchaseResponseDTO>> regenerateQR(
            @PathVariable UUID purchaseId) {
        return ResponseEntity.ok(ApiResponse.success("QR code regenerated",
                qrService.regenerateQR(purchaseId)));
    }
}
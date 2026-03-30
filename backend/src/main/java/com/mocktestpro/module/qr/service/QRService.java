package com.mocktestpro.module.qr.service;

import com.mocktestpro.module.qr.dto.QRValidationDTO;
import com.mocktestpro.module.payment.dto.PurchaseResponseDTO;
import java.util.UUID;

public interface QRService {

    QRValidationDTO validateQRCode(String qrJwtToken);

    QRValidationDTO getQRStatus(UUID purchaseId);

    PurchaseResponseDTO regenerateQR(UUID purchaseId);
}
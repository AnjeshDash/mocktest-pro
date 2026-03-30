package com.mocktestpro.module.qr.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.UUID;

@Getter
@Builder
public class QRValidationDTO {

    private boolean valid;
    private String message;
    private UUID testId;
    private UUID purchaseId;
    private UUID userId;
}
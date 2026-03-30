package com.mocktestpro.common.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class QRCodeUtil {

    @Value("${app.jwt.secret:change-this-secret-in-production-min-32-chars}")
    private String jwtSecret;

    @Value("${app.qr.expiry-hours:24}")
    private int qrExpiryHours;

    @Value("${app.qr.width:300}")
    private int qrWidth;

    @Value("${app.qr.height:300}")
    private int qrHeight;

    public String generateQrJwt(UUID userId, UUID testId, UUID purchaseId) {
        SecretKey key = getSigningKey();
        long expirySeconds = qrExpiryHours * 60L * 60L;
        return Jwts.builder()
                .subject(userId.toString())
                .claim("testId", testId.toString())
                .claim("purchaseId", purchaseId.toString())
                .claim("type", "QR_ACCESS")
                .issuedAt(new java.util.Date())
                .expiration(java.util.Date.from(Instant.now().plusSeconds(expirySeconds)))
                .signWith(key)
                .compact();
    }

    public String generateQrCodeBase64(String content) {
        try {
            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.MARGIN, 2);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, qrWidth, qrHeight, hints);

            BufferedImage image = new BufferedImage(qrWidth, qrHeight, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < qrWidth; x++) {
                for (int y = 0; y < qrHeight; y++) {
                    image.setRGB(x, y, matrix.get(x, y) ? 0x000000 : 0xFFFFFF);
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", baos);
            return Base64.getEncoder().encodeToString(baos.toByteArray());

        } catch (WriterException | IOException e) {
            log.error("Failed to generate QR code: {}", e.getMessage());
            throw new RuntimeException("QR code generation failed", e);
        }
    }

    public Claims validateQrJwt(String jwtToken) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(jwtToken)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.warn("QR token expired: {}", e.getMessage());
            throw e;
        } catch (JwtException e) {
            log.warn("Invalid QR token: {}", e.getMessage());
            throw new RuntimeException("Invalid JWT token", e);
        }
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
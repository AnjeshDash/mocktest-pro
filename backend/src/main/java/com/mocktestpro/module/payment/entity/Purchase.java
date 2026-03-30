package com.mocktestpro.module.payment.entity;

import com.mocktestpro.module.test.entity.MockTest;
import com.mocktestpro.module.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "purchases",
        uniqueConstraints = @UniqueConstraint(columnNames = {"attendee_id", "mock_test_id"}))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendee_id", nullable = false)
    private User attendee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mock_test_id", nullable = false)
    private MockTest mockTest;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amountPaid;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(length = 50)
    @Builder.Default
    private String paymentMethod = "SIMULATED";

    @Column(unique = true, length = 255)
    private String transactionId;

    @Column(columnDefinition = "TEXT")
    private String qrCodeData;

    @Column(nullable = false)
    @Builder.Default
    private boolean isQrUsed = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime purchasedAt;

    @Column
    private LocalDateTime expiresAt;
}
package com.mocktestpro.module.exam.entity;

import com.mocktestpro.module.payment.entity.Purchase;
import com.mocktestpro.module.test.entity.MockTest;
import com.mocktestpro.module.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "exam_sessions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id", nullable = false, unique = true)
    private Purchase purchase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendee_id", nullable = false)
    private User attendee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mock_test_id", nullable = false)
    private MockTest mockTest;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private ExamSessionStatus status = ExamSessionStatus.IN_PROGRESS;

    @Column(nullable = false)
    @Builder.Default
    private Integer currentSectionIndex = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer currentQuestionIndex = 0;

    @Column
    private LocalDateTime sectionStartTime;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime startedAt;

    @Column
    private LocalDateTime submittedAt;

    @Column(length = 45)
    private String ipAddress;

    @Column(columnDefinition = "TEXT")
    private String userAgent;

    @Column(nullable = false)
    @Builder.Default
    private Integer tabSwitchCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private boolean isFlagged = false;
}
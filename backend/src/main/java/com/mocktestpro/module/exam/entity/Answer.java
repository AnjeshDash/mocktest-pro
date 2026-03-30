package com.mocktestpro.module.exam.entity;

import com.mocktestpro.module.test.entity.Question;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "answers", uniqueConstraints = @UniqueConstraint(
        columnNames = {"exam_session_id", "question_id"}))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_session_id", nullable = false)
    private ExamSession examSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(length = 500)
    private String selectedAnswer;

    @Column
    private Boolean isCorrect;

    @Column(precision = 4, scale = 2)
    @Builder.Default
    private BigDecimal marksAwarded = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private Integer timeSpentSeconds = 0;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime answeredAt;
}
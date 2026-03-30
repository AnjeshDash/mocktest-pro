package com.mocktestpro.module.test.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "questions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String questionText;

    @Column(nullable = false, length = 30)
    private String questionType;

    @Column(columnDefinition = "TEXT")
    private String options;

    @Column(nullable = false, length = 500)
    private String correctAnswer;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String solution;

    @Column(length = 500)
    private String solutionImageUrl;

    @Column(nullable = false, precision = 4, scale = 2)
    @Builder.Default
    private BigDecimal marks = BigDecimal.ONE;

    @Column(nullable = false, precision = 4, scale = 2)
    @Builder.Default
    private BigDecimal negativeMarks = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private Difficulty difficulty = Difficulty.MEDIUM;

    @Column(length = 200)
    private String topic;

    @Column(length = 500)
    private String imageUrl;

    @Column(nullable = false)
    private Integer orderIndex;
}
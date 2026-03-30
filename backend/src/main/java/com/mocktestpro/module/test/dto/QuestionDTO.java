package com.mocktestpro.module.test.dto;

import com.mocktestpro.module.test.entity.Difficulty;
import com.mocktestpro.module.test.entity.Question;
import com.mocktestpro.module.test.entity.Section;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO {

    private UUID id;

    @NotBlank(message = "Question text is required")
    private String questionText;

    @NotBlank(message = "Question type is required")
    private String questionType;

    private String options;

    @NotBlank(message = "Correct answer is required")
    private String correctAnswer;

    @NotBlank(message = "Solution is required")
    private String solution;

    private String solutionImageUrl;

    @NotNull(message = "Marks are required")
    private BigDecimal marks;

    private BigDecimal negativeMarks;

    private Difficulty difficulty;

    private String topic;

    private String imageUrl;

    @NotNull(message = "Order index is required")
    private Integer orderIndex;

    public static QuestionDTO fromEntity(Question q) {
        return QuestionDTO.builder()
                .id(q.getId())
                .questionText(q.getQuestionText())
                .questionType(q.getQuestionType())
                .options(q.getOptions())
                .correctAnswer(q.getCorrectAnswer())
                .solution(q.getSolution())
                .solutionImageUrl(q.getSolutionImageUrl())
                .marks(q.getMarks())
                .negativeMarks(q.getNegativeMarks())
                .difficulty(q.getDifficulty())
                .topic(q.getTopic())
                .imageUrl(q.getImageUrl())
                .orderIndex(q.getOrderIndex())
                .build();
    }

    public Question toEntity(Section section) {
        return Question.builder()
                .section(section)
                .questionText(questionText)
                .questionType(questionType)
                .options(options)
                .correctAnswer(correctAnswer)
                .solution(solution)
                .solutionImageUrl(solutionImageUrl)
                .marks(marks != null ? marks : BigDecimal.ONE)
                .negativeMarks(negativeMarks != null ? negativeMarks : BigDecimal.ZERO)
                .difficulty(difficulty != null ? difficulty : Difficulty.MEDIUM)
                .topic(topic)
                .imageUrl(imageUrl)
                .orderIndex(orderIndex)
                .build();
    }
}
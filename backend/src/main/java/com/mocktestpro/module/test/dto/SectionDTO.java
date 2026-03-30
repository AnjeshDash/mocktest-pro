package com.mocktestpro.module.test.dto;

import com.mocktestpro.module.test.entity.Section;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SectionDTO {

    private UUID id;

    @NotBlank(message = "Section name is required")
    private String name;

    private String description;

    @NotNull(message = "Duration is required")
    private Integer durationMinutes;

    @NotNull(message = "Order index is required")
    private Integer orderIndex;

    @NotNull(message = "Total marks are required")
    private Integer totalMarks;

    private Integer passingMarks;

    @Valid
    private List<QuestionDTO> questions;

    public static SectionDTO fromEntity(Section section, boolean includeQuestions) {
        SectionDTO dto = SectionDTO.builder()
                .id(section.getId())
                .name(section.getName())
                .description(section.getDescription())
                .durationMinutes(section.getDurationMinutes())
                .orderIndex(section.getOrderIndex())
                .totalMarks(section.getTotalMarks())
                .passingMarks(section.getPassingMarks())
                .build();

        if (includeQuestions && section.getQuestions() != null) {
            dto.setQuestions(section.getQuestions().stream()
                    .map(QuestionDTO::fromEntity)
                    .collect(Collectors.toList()));
        }
        return dto;
    }
}
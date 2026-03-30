package com.mocktestpro.module.test.dto;

import com.mocktestpro.module.test.entity.Difficulty;
import com.mocktestpro.module.test.entity.MockTest;
import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Builder
public class MockTestResponseDTO {

    private UUID id;
    private String title;
    private String description;
    private String category;
    private String subCategory;
    private Difficulty difficulty;
    private BigDecimal price;
    private Integer durationMinutes;
    private Integer totalMarks;
    private Integer passingMarks;
    private Integer maxAttempts;
    private boolean published;
    private String thumbnailUrl;
    private Integer version;
    private LocalDateTime expiresAt;
    private List<String> tags;
    private boolean expired;
    private String organizerName;
    private UUID organizerId;
    private List<SectionDTO> sections;
    private int totalSections;
    private long totalQuestions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static MockTestResponseDTO fromEntity(MockTest t, boolean includeQuestions) {
        List<SectionDTO> sectionDTOs = t.getSections() == null ? List.of() :
                t.getSections().stream()
                        .map(s -> SectionDTO.fromEntity(s, includeQuestions))
                        .collect(Collectors.toList());

        long qCount = sectionDTOs.stream()
                .mapToLong(s -> s.getQuestions() != null ? s.getQuestions().size() : 0)
                .sum();

        return MockTestResponseDTO.builder()
                .id(t.getId())
                .title(t.getTitle())
                .description(t.getDescription())
                .category(t.getCategory())
                .subCategory(t.getSubCategory())
                .difficulty(t.getDifficulty())
                .price(t.getPrice())
                .durationMinutes(t.getDurationMinutes())
                .totalMarks(t.getTotalMarks())
                .passingMarks(t.getPassingMarks())
                .maxAttempts(t.getMaxAttempts())
                .published(t.isPublished())
                .thumbnailUrl(t.getThumbnailUrl())
                .version(t.getVersion())
                .expiresAt(t.getExpiresAt())
                .tags(t.getTagsList())
                .expired(t.isExpired())
                .organizerName(t.getOrganizer() != null ? t.getOrganizer().getFullName() : null)
                .organizerId(t.getOrganizer() != null ? t.getOrganizer().getId() : null)
                .sections(sectionDTOs)
                .totalSections(sectionDTOs.size())
                .totalQuestions(qCount)
                .createdAt(t.getCreatedAt())
                .updatedAt(t.getUpdatedAt())
                .build();
    }
}
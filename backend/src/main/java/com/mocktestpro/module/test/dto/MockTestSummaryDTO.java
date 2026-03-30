package com.mocktestpro.module.test.dto;

import com.mocktestpro.module.test.entity.Difficulty;
import com.mocktestpro.module.test.entity.MockTest;
import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class MockTestSummaryDTO {

    private UUID id;
    private String title;
    private String category;
    private String subCategory;
    private Difficulty difficulty;
    private BigDecimal price;
    private Integer durationMinutes;
    private Integer totalMarks;
    private Integer passingMarks;
    private String thumbnailUrl;
    private Integer version;
    private LocalDateTime expiresAt;
    private List<String> tags;
    private boolean expired;
    private String organizerName;
    private LocalDateTime createdAt;

    public static MockTestSummaryDTO fromEntity(MockTest t) {
        return MockTestSummaryDTO.builder()
                .id(t.getId())
                .title(t.getTitle())
                .category(t.getCategory())
                .subCategory(t.getSubCategory())
                .difficulty(t.getDifficulty())
                .price(t.getPrice())
                .durationMinutes(t.getDurationMinutes())
                .totalMarks(t.getTotalMarks())
                .passingMarks(t.getPassingMarks())
                .thumbnailUrl(t.getThumbnailUrl())
                .version(t.getVersion())
                .expiresAt(t.getExpiresAt())
                .tags(t.getTagsList())
                .expired(t.isExpired())
                .organizerName(t.getOrganizer() != null ? t.getOrganizer().getFullName() : null)
                .createdAt(t.getCreatedAt())
                .build();
    }
}
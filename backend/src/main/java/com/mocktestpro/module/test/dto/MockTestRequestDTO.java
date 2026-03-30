package com.mocktestpro.module.test.dto;

import com.mocktestpro.module.test.entity.Difficulty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MockTestRequestDTO {

    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 300, message = "Title must be 5-300 characters")
    private String title;

    private String description;

    @NotBlank(message = "Category is required")
    private String category;

    private String subCategory;

    @NotNull(message = "Difficulty is required")
    private Difficulty difficulty;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.00", message = "Price cannot be negative")
    private BigDecimal price;

    @NotNull(message = "Duration is required")
    @Min(value = 5, message = "Duration must be at least 5 minutes")
    private Integer durationMinutes;

    @NotNull(message = "Total marks required")
    @Min(value = 1, message = "Total marks must be at least 1")
    private Integer totalMarks;

    @NotNull(message = "Passing marks required")
    private Integer passingMarks;

    private Integer maxAttempts;

    private String thumbnailUrl;

    private LocalDateTime expiresAt;

    private List<String> tags;

    @Valid
    private List<SectionDTO> sections;

    public String getTagsAsString() {
        if (tags == null || tags.isEmpty()) return null;
        return String.join(",", tags);
    }
}
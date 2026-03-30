package com.mocktestpro.module.test.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestFilterDTO {
    private String category;
    private String difficulty;
    private Double maxPrice;
    private String keyword;
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDir;
}
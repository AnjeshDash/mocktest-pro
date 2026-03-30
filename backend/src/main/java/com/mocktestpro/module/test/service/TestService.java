package com.mocktestpro.module.test.service;

import com.mocktestpro.module.test.dto.MockTestRequestDTO;
import com.mocktestpro.module.test.dto.MockTestResponseDTO;
import com.mocktestpro.module.test.dto.MockTestSummaryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.UUID;

public interface TestService {

    // Public endpoints
    Page<MockTestSummaryDTO> getPublishedTests(Pageable pageable);
    Page<MockTestSummaryDTO> filterTests(String category, String difficulty, String maxPrice, Pageable pageable);
    Page<MockTestSummaryDTO> searchTests(String query, Pageable pageable);
    MockTestResponseDTO getTestById(UUID id);
    List<String> getAllCategories();

    // Organizer endpoints
    MockTestResponseDTO createTest(MockTestRequestDTO request);
    MockTestResponseDTO updateTest(UUID id, MockTestRequestDTO request);
    void deleteTest(UUID id);
    MockTestResponseDTO togglePublish(UUID id);
    MockTestResponseDTO uploadQuestions(UUID id, MockTestRequestDTO jsonData);
    Page<MockTestSummaryDTO> getMyTests(Pageable pageable);
    MockTestResponseDTO getMyTestById(UUID id);
}
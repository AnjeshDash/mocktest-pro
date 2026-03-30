package com.mocktestpro.module.test.controller;

import com.mocktestpro.common.response.ApiResponse;
import com.mocktestpro.module.test.dto.MockTestRequestDTO;
import com.mocktestpro.module.test.dto.MockTestResponseDTO;
import com.mocktestpro.module.test.dto.MockTestSummaryDTO;
import com.mocktestpro.module.test.service.TestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/tests")
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    // ==================== PUBLIC ENDPOINTS ====================

    @GetMapping
    public ResponseEntity<ApiResponse<Page<MockTestSummaryDTO>>> getTests(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String maxPrice,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, Math.min(size, 50), sort);

        Page<MockTestSummaryDTO> result;
        if (q != null && !q.isBlank()) {
            result = testService.searchTests(q, pageable);
        } else if (category != null || difficulty != null || maxPrice != null) {
            result = testService.filterTests(category, difficulty, maxPrice, pageable);
        } else {
            result = testService.getPublishedTests(pageable);
        }

        return ResponseEntity.ok(ApiResponse.success("Tests fetched", result));
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<String>>> getCategories() {
        return ResponseEntity.ok(ApiResponse.success("Categories fetched", testService.getAllCategories()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MockTestResponseDTO>> getTestById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success("Test found", testService.getTestById(id)));
    }

    // ==================== ORGANIZER ENDPOINTS ====================

    @GetMapping("/my-tests")
    @PreAuthorize("hasRole('ROLE_ORGANIZER')")
    public ResponseEntity<ApiResponse<Page<MockTestSummaryDTO>>> getMyTests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.success("Your tests", testService.getMyTests(pageable)));
    }

    @GetMapping("/my-tests/{id}")
    @PreAuthorize("hasRole('ROLE_ORGANIZER')")
    public ResponseEntity<ApiResponse<MockTestResponseDTO>> getMyTestById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success("Test details", testService.getMyTestById(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ORGANIZER')")
    public ResponseEntity<ApiResponse<MockTestResponseDTO>> createTest(
            @Valid @RequestBody MockTestRequestDTO request) {
        MockTestResponseDTO created = testService.createTest(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Test created successfully", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ORGANIZER')")
    public ResponseEntity<ApiResponse<MockTestResponseDTO>> updateTest(
            @PathVariable UUID id,
            @Valid @RequestBody MockTestRequestDTO request) {
        return ResponseEntity.ok(ApiResponse.success("Test updated", testService.updateTest(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ORGANIZER')")
    public ResponseEntity<ApiResponse<?>> deleteTest(@PathVariable UUID id) {
        testService.deleteTest(id);
        return ResponseEntity.ok(ApiResponse.success("Test deleted successfully"));
    }

    @PatchMapping("/{id}/publish")
    @PreAuthorize("hasRole('ROLE_ORGANIZER')")
    public ResponseEntity<ApiResponse<MockTestResponseDTO>> togglePublish(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success("Publish status toggled", testService.togglePublish(id)));
    }

    @PostMapping("/{id}/upload")
    @PreAuthorize("hasRole('ROLE_ORGANIZER')")
    public ResponseEntity<ApiResponse<MockTestResponseDTO>> uploadQuestions(
            @PathVariable UUID id,
            @Valid @RequestBody MockTestRequestDTO request) {
        MockTestResponseDTO result = testService.uploadQuestions(id, request);
        return ResponseEntity.ok(ApiResponse.success(
                "Questions uploaded successfully. Version: " + result.getVersion(), result));
    }
}
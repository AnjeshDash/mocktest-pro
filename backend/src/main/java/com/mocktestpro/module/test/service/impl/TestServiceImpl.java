package com.mocktestpro.module.test.service.impl;

import com.mocktestpro.common.exception.BusinessException;
import com.mocktestpro.common.exception.ResourceNotFoundException;
import com.mocktestpro.common.util.SecurityUtils;
import com.mocktestpro.module.test.dto.*;
import com.mocktestpro.module.test.entity.*;
import com.mocktestpro.module.test.repository.*;
import com.mocktestpro.module.test.service.TestService;
import com.mocktestpro.module.user.entity.User;
import com.mocktestpro.module.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final TestRepository testRepository;
    private final SectionRepository sectionRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;

    // ==================== PUBLIC METHODS ====================

    @Override
    @Transactional(readOnly = true)
    public Page<MockTestSummaryDTO> getPublishedTests(Pageable pageable) {
        return testRepository
                .findByIsPublishedTrueAndIsActiveTrue(pageable)
                .map(MockTestSummaryDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MockTestSummaryDTO> filterTests(String category, String difficulty, String maxPrice, Pageable pageable) {
        Difficulty diff = null;
        if (difficulty != null && !difficulty.isBlank()) {
            try {
                diff = Difficulty.valueOf(difficulty.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw BusinessException.badRequest("INVALID_DIFFICULTY",
                        "Difficulty must be: EASY, MEDIUM, HARD, EXPERT");
            }
        }
        BigDecimal price = maxPrice != null && !maxPrice.isBlank()
                ? new BigDecimal(maxPrice) : null;

        return testRepository
                .findWithFilters(category, diff, price, pageable)
                .map(MockTestSummaryDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MockTestSummaryDTO> searchTests(String query, Pageable pageable) {
        if (query == null || query.isBlank()) {
            return getPublishedTests(pageable);
        }
        return testRepository
                .findByTitleContainingIgnoreCaseAndIsPublishedTrueAndIsActiveTrue(query, pageable)
                .map(MockTestSummaryDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public MockTestResponseDTO getTestById(UUID id) {
        MockTest test = testRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MockTest", "id", id));
        return MockTestResponseDTO.fromEntity(test, false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllCategories() {
        return List.of("TCS NQT", "SSC CGL", "IBPS PO", "GATE", "UPSC", "Wipro", "Infosys");
    }

    // ==================== ORGANIZER METHODS ====================

    @Override
    @Transactional
    public MockTestResponseDTO createTest(MockTestRequestDTO request) {
        User organizer = getCurrentOrganizer();

        MockTest test = MockTest.builder()
                .organizer(organizer)
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .subCategory(request.getSubCategory())
                .difficulty(request.getDifficulty())
                .price(request.getPrice())
                .durationMinutes(request.getDurationMinutes())
                .totalMarks(request.getTotalMarks())
                .passingMarks(request.getPassingMarks())
                .maxAttempts(request.getMaxAttempts() != null ? request.getMaxAttempts() : 1)
                .thumbnailUrl(request.getThumbnailUrl())
                .expiresAt(request.getExpiresAt())
                .tags(request.getTagsAsString())
                .isPublished(false)
                .build();

        MockTest saved = testRepository.save(test);
        log.info("Test created: {} by organizer: {}", saved.getId(), organizer.getEmail());
        return MockTestResponseDTO.fromEntity(saved, false);
    }

    @Override
    @Transactional
    public MockTestResponseDTO updateTest(UUID id, MockTestRequestDTO request) {
        MockTest test = getOwnedTest(id);

        test.setTitle(request.getTitle());
        test.setDescription(request.getDescription());
        test.setCategory(request.getCategory());
        test.setSubCategory(request.getSubCategory());
        test.setDifficulty(request.getDifficulty());
        test.setPrice(request.getPrice());
        test.setDurationMinutes(request.getDurationMinutes());
        test.setTotalMarks(request.getTotalMarks());
        test.setPassingMarks(request.getPassingMarks());
        test.setThumbnailUrl(request.getThumbnailUrl());
        test.setExpiresAt(request.getExpiresAt());
        test.setTags(request.getTagsAsString());

        return MockTestResponseDTO.fromEntity(testRepository.save(test), false);
    }

    @Override
    @Transactional
    public void deleteTest(UUID id) {
        MockTest test = getOwnedTest(id);
        test.setActive(false);
        testRepository.save(test);
        log.info("Test soft-deleted: {}", id);
    }

    @Override
    @Transactional
    public MockTestResponseDTO togglePublish(UUID id) {
        MockTest test = getOwnedTest(id);

        if (!test.isPublished()) {
            long sectionCount = sectionRepository.countByMockTest_Id(id);
            if (sectionCount == 0) {
                throw BusinessException.badRequest("NO_SECTIONS",
                        "Cannot publish a test with no sections. Upload questions first.");
            }
        }

        test.setPublished(!test.isPublished());
        log.info("Test {} published={}", id, test.isPublished());
        return MockTestResponseDTO.fromEntity(testRepository.save(test), false);
    }

    @Override
    @Transactional
    public MockTestResponseDTO uploadQuestions(UUID id, MockTestRequestDTO jsonData) {
        MockTest test = getOwnedTest(id);

        // Delete all existing sections and questions first
        sectionRepository.deleteByMockTest_Id(id);
        test.getSections().clear();
        test.setVersion(test.getVersion() + 1);

        if (jsonData.getSections() != null) {
            for (SectionDTO sectionDTO : jsonData.getSections()) {
                Section section = Section.builder()
                        .mockTest(test)
                        .name(sectionDTO.getName())
                        .description(sectionDTO.getDescription())
                        .durationMinutes(sectionDTO.getDurationMinutes())
                        .orderIndex(sectionDTO.getOrderIndex())
                        .totalMarks(sectionDTO.getTotalMarks())
                        .passingMarks(sectionDTO.getPassingMarks())
                        .build();
                Section savedSection = sectionRepository.save(section);

                if (sectionDTO.getQuestions() != null) {
                    List<Question> questions = sectionDTO.getQuestions().stream()
                            .map(qDTO -> qDTO.toEntity(savedSection))
                            .collect(Collectors.toList());
                    questionRepository.saveAll(questions);
                }
            }
        }

        MockTest saved = testRepository.save(test);
        log.info("Questions uploaded for test: {}, version: {}", id, saved.getVersion());

        MockTest reloaded = testRepository.findById(id).orElse(saved);
        return MockTestResponseDTO.fromEntity(reloaded, true);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MockTestSummaryDTO> getMyTests(Pageable pageable) {
        User organizer = getCurrentOrganizer();
        return testRepository
                .findByOrganizer_IdAndIsActiveTrue(organizer.getId(), pageable)
                .map(MockTestSummaryDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public MockTestResponseDTO getMyTestById(UUID id) {
        MockTest test = getOwnedTest(id);
        MockTest withSections = testRepository.findById(id).orElse(test);
        return MockTestResponseDTO.fromEntity(withSections, true);
    }

    // ==================== PRIVATE HELPERS ====================

    private User getCurrentOrganizer() {
        String keycloakId = securityUtils.getCurrentKeycloakId();
        return userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "keycloakId", keycloakId));
    }

    private MockTest getOwnedTest(UUID testId) {
        User organizer = getCurrentOrganizer();
        MockTest test = testRepository.findById(testId)
                .filter(MockTest::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("MockTest", "id", testId));

        if (!test.getOrganizer().getId().equals(organizer.getId())) {
            throw BusinessException.forbidden("NOT_YOUR_TEST",
                    "You can only manage your own tests");
        }
        return test;
    }
}
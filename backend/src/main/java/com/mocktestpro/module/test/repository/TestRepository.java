package com.mocktestpro.module.test.repository;

import com.mocktestpro.module.test.entity.Difficulty;
import com.mocktestpro.module.test.entity.MockTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface TestRepository extends JpaRepository<MockTest, UUID> {

    Page<MockTest> findByIsPublishedTrueAndIsActiveTrue(Pageable pageable);

    Page<MockTest> findByOrganizer_IdAndIsActiveTrue(UUID organizerId, Pageable pageable);

    // ADD THIS METHOD
    Page<MockTest> findByTitleContainingIgnoreCaseAndIsPublishedTrueAndIsActiveTrue(String title, Pageable pageable);

    @Query("SELECT t FROM MockTest t WHERE t.isPublished = true AND t.isActive = true " +
            "AND (:category IS NULL OR t.category = :category) " +
            "AND (:difficulty IS NULL OR t.difficulty = :difficulty) " +
            "AND (:maxPrice IS NULL OR t.price <= :maxPrice)")
    Page<MockTest> findWithFilters(@Param("category") String category,
                                   @Param("difficulty") Difficulty difficulty,
                                   @Param("maxPrice") java.math.BigDecimal maxPrice,
                                   Pageable pageable);
}
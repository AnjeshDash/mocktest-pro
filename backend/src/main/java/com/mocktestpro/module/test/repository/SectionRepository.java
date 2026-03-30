package com.mocktestpro.module.test.repository;

import com.mocktestpro.module.test.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface SectionRepository extends JpaRepository<Section, UUID> {

    List<Section> findByMockTest_IdOrderByOrderIndexAsc(UUID testId);

    @Query("SELECT DISTINCT s FROM Section s LEFT JOIN FETCH s.questions WHERE s.mockTest.id = :testId ORDER BY s.orderIndex ASC")
    List<Section> findByTestIdWithQuestions(@Param("testId") UUID testId);

    long countByMockTest_Id(UUID testId);

    @Modifying
    void deleteByMockTest_Id(UUID testId);
}